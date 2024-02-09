import com.sap.gateway.ip.core.customdev.util.Message
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
        def msgBody = this.getClass().getResource('input1.xml').newInputStream()
        msg.setBody(msgBody)

        when: 'we execute the Groovy script'
        script.processData(msg)

        then: 'the output message body is as expected'
        msg.getBody(String) == this.getClass().getResource('output1.xml').text.normalize()
    }

    def 'Scenario 2 - Order does not have items'() {
        given: 'the message body and property are initialized'
        def msgBody = this.getClass().getResource('input2.xml').newInputStream()
        msg.setBody(msgBody)
        msg.setProperty('DocType', 'HDR')

        when: 'we execute the Groovy script'
        script.processData(msg)

        then: 'the output message body is as expected'
        msg.getBody(String) == this.getClass().getResource('output2.xml').text.normalize()
    }
}