import com.sap.gateway.ip.core.customdev.util.Message
import com.sap.it.api.mapping.ValueMappingApi
import spock.lang.Shared
import spock.lang.Specification

class XMLTransformationSpec extends Specification {
    @Shared
    Script script
    Message msg

    def setupSpec() {
        GroovyShell shell = new GroovyShell()
        script = shell.parse(this.getClass().getResource('/script/XMLTransformation.groovy').toURI())
    }

    def setup() {
        msg = new Message()
    }

    def 'Scenario 1 - Order has items'() {
        given: 'the message body is initialized'
        msg.setBody(this.getClass().getResource('input1.xml').newInputStream())

        when: 'we execute the Groovy script'
        script.processData(msg)

        then: 'the output message body is as expected'
        def root = new XmlSlurper().parse(msg.getBody(Reader))
        verifyAll {
            root.Header.ID.text() == 'ORD60001'
            root.Header.DocumentDate.text() == '2019-02-18'
            root.Header.DocumentType.text() == ''
            root.Item.size() == 1
            root.Item.ItemNumber.text() == '010'
            root.Item.ProductCode.text() == 'MT70001'
            root.Item.Quantity.text() == '57'
        }
    }

    def 'Scenario 2 - Order does not have items'() {
        given: 'the message body and property are initialized'
        msg.setBody(this.getClass().getResource('input2.xml').newInputStream())
        msg.setProperty('DocType', 'HDR')

        // Set up value mapping entries
        ValueMappingApi vmapi = ValueMappingApi.getInstance()
        vmapi.addEntry('S4', 'DocType', 'HDR', 'ACME', 'DocumentType', 'ACME-HDR')

        when: 'we execute the Groovy script'
        script.processData(msg)

        then: 'the output message body is as expected'
        def root = new XmlSlurper().parse(msg.getBody(Reader))
        verifyAll {
            root.Header.ID.text() == 'ORD80002'
            root.Header.DocumentDate.text() == '2020-02-18'
            root.Header.DocumentType.text() == 'ACME-HDR'
            root.Item.size() == 0
        }
    }
}