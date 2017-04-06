package grails.plugin.formfields.taglib

import grails.plugin.formfields.mock.Person
import spock.lang.Issue
import grails.plugin.formfields.*
import grails.test.mixin.*

@Issue('https://github.com/grails-fields-plugin/grails-fields/pull/51')
@TestFor(FormFieldsTagLib)
@Mock(Person)
class FieldNamePrefixSpec extends AbstractFormFieldsTagLibSpec {

	def mockFormFieldsTemplateService = Mock(FormFieldsTemplateService)

	def setupSpec() {
		configurePropertyAccessorSpringBean()
	}

	def setup() {
		def taglib = applicationContext.getBean(FormFieldsTagLib)

		mockFormFieldsTemplateService.findTemplate(_, 'wrapper', null, null) >> [path: '/_fields/default/wrapper']
        mockFormFieldsTemplateService.getTemplateFor('wrapper') >> "wrapper"
        mockFormFieldsTemplateService.getTemplateFor('widget') >> "widget"
        mockFormFieldsTemplateService.getTemplateFor('displayWrapper') >> "displayWrapper"
        mockFormFieldsTemplateService.getTemplateFor('displayWidget') >> "displayWidget"
		mockFormFieldsTemplateService.getWidgetPrefix() >> 'input-'
		taglib.formFieldsTemplateService = mockFormFieldsTemplateService

		mockEmbeddedSitemeshLayout(taglib)
	}

	void 'a prefix can be added to the field names generated by f:field'() {
		given:
		views["/_fields/default/_wrapper.gsp"] = '${widget}'
		views["/_fields/person/name/_widget.gsp"] = '${prefix}${property}'

		and:
		mockFormFieldsTemplateService.findTemplate(_, 'widget', null, null) >> [path: '/_fields/person/name/widget']

		expect:
		applyTemplate('<f:field bean="personInstance" property="name" prefix="foo"/>', [personInstance: personInstance]) == 'foo.name'
	}

	void 'a prefix can be added to the field names generated by f:all'() {
		given:
		views["/_fields/default/_wrapper.gsp"] = '${prefix}${property} '

		expect:
		applyTemplate('<f:all bean="personInstance" prefix="foo"/>', [personInstance: personInstance]).startsWith('foo.salutation foo.name foo.dateOfBirth')
	}

	void 'a prefix is added to any embedded field names by f:all'() {
		given:
		views["/_fields/default/_wrapper.gsp"] = '${prefix}${property} '

		expect:
		applyTemplate('<f:all bean="personInstance" prefix="foo"/>', [personInstance: personInstance]).contains('foo.address.street foo.address.city foo.address.country')
	}

	void 'a prefix can be added to the field names generated by fields rendered inside f:with'() {
		given:
		views["/_fields/default/_wrapper.gsp"] = '${prefix}${property}'

		expect:
		applyTemplate('<f:with bean="personInstance" prefix="foo"><f:field property="name"/></f:with>', [personInstance: personInstance]) == 'foo.name'
	}

	void 'a prefix attribute on f:field overrides one inherited from f:with'() {
		given:
		views["/_fields/default/_wrapper.gsp"] = '${prefix}${property}'

		expect:
		applyTemplate('<f:with bean="personInstance" prefix="foo"><f:field property="name" prefix="bar"/></f:with>', [personInstance: personInstance]) == 'bar.name'
	}

}