package com.github.d3rwan.dbtoes;

import org.elasticsearch.client.Client;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.github.d3rwan.dbtoes.common.Constants;
import com.github.d3rwan.dbtoes.config.DatabaseConfig;
import com.github.d3rwan.dbtoes.config.ESConfig;
import com.github.d3rwan.dbtoes.models.Person;
import com.github.d3rwan.dbtoes.processors.PersonItemProcessor;
import com.github.d3rwan.toes.models.ESDocument;
import com.github.d3rwan.toes.writers.ESItemWriter;

@Configuration
@Import({DatabaseConfig.class, ESConfig.class})
@EnableBatchProcessing
public class BatchConfiguration {

	/** environment */
	@Autowired
	private Environment environment;

	/** session factory */
	@Autowired
	private SessionFactory sessionFactory;

	/** ES client */
	@Autowired
	private Client esClient;

	/**
	 * Read into DB
	 * @return HibernateCursorItemReader
	 */
	@Bean
	public HibernateCursorItemReader<Person> reader(SessionFactory sessionFactory) {
		HibernateCursorItemReader<Person> reader = new HibernateCursorItemReader<Person>();
		reader.setSessionFactory(sessionFactory);
		reader.setQueryString("from Person p");
		return reader;
	}

	/**
	 * Process person into ESDocument
	 * @return ItemProcessor
	 */
	@Bean
	public ItemProcessor<Person, ESDocument> processor() {
		return new PersonItemProcessor();
	}

	/**
	 * Writer for ES
	 * @param esClient ES client
	 * @return ItemWriter
	 */
	@Bean
	public ItemWriter<ESDocument> writer() {
		ESItemWriter<ESDocument> writer = new ESItemWriter<ESDocument>(
				esClient, environment.getProperty(Constants.CONFIG_ES_TIMEOUT));
		return writer;
	}

	/**
	 * Job : Import user from DB into ES
	 * @param jobBuilderFactory factory
	 * @param step1 step to add
	 * @return Job
	 */
	@Bean
	public Job importUserJob(JobBuilderFactory jobBuilderFactory, Step step1) {
		return jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1)
				.end()
				.build();
	}

	/**
	 * Step 1 : Read from DB, process into ESDocument, index into ES
	 * @param stepBuilderFactory factory
	 * @param reader reader
	 * @param writer writer
	 * @param processor processor
	 * @return Step
	 */
	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Person> reader,
			ItemWriter<ESDocument> writer, ItemProcessor<Person, ESDocument> processor) {
		return stepBuilderFactory.get("step1").<Person, ESDocument> chunk(1000)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}