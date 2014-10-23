package com.github.d3rwan.dbtoes.processors;

import com.github.d3rwan.dbtoes.common.Constants;
import com.github.d3rwan.dbtoes.models.Person;
import com.github.d3rwan.toes.models.ESDocument;
import com.github.d3rwan.toes.processors.AbstractESItemProcessor;

/**
 * Processor - Transform person into ESDocument
 * 
 * @author d3rwan
 * 
 */
public class PersonItemProcessor extends AbstractESItemProcessor<Person> {

	@Override
	public ESDocument process(Person item) throws Exception {
		ESDocument document = new ESDocument();
		document.setIndex(environment.getProperty(Constants.CONFIG_ES_INDEX));
		document.setType(environment.getProperty(Constants.CONFIG_ES_TYPE));
		document.setId(item.getId());
		document.setSource(mapper.writeValueAsString(item));
		return document;
	}
}
