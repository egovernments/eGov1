package org.egov.ptis.client.service.calculator;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Date;
import java.util.HashMap;

import org.egov.builder.entities.BoundaryBuilder;
import org.egov.commons.Installment;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.ptis.builder.entity.property.BasicPropertyBuilder;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.model.calculator.TaxCalculationInfo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class APTaxCalculatorTest {
	@Autowired
	private APTaxCalculator taxCalculator;

	private Boundary locality;
	private Property property;
	private BasicProperty basicProperty;
	private HashMap<Installment, TaxCalculationInfo> taxInfo = new HashMap<Installment, TaxCalculationInfo>();

	@Before
	public void before() {
		initMocks(this);
		initMasters();
		initProperty();
	}

	private void initMasters() {
		locality = new BoundaryBuilder().withDefaults().build();
	}

	private void initProperty() {
		basicProperty = new BasicPropertyBuilder().withDefaults().build();
		property = basicProperty.getProperty();
	}

	@Ignore
	public void calculatePropertyTax() {
		taxInfo = taxCalculator.calculatePropertyTax(property, new Date());
	}
}
