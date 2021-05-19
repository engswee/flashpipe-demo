import com.sap.gateway.ip.core.customdev.util.Message
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import spock.lang.Shared
import spock.lang.Specification

class XMLTransformationSpec extends Specification {
  @Shared Script script
  @Shared CamelContext context
  Message msg
  Exchange exchange

  def setupSpec() {
    GroovyShell shell = new GroovyShell()
    script = shell.parse(this.getClass().getResource('/script/XMLTransformation.groovy').toURI())
    context = new DefaultCamelContext()
  }

  def setup() {
    exchange = new DefaultExchange(context)
    msg = new Message(exchange)
  }

  def 'Scenario 1 - Order has items'() {
    given: 'the message body is initialized'
    def msgBody = this.getClass().getResource('input1.xml').newInputStream()
    exchange.getIn().setBody(msgBody)
    msg.setBody(exchange.getIn().getBody())

    when: 'we execute the Groovy script'
    script.processData(msg)
    exchange.getIn().setBody(msg.getBody())

    then: 'the output message body is as expected'
    msg.getBody(String) == this.getClass().getResource('output1.xml').text.normalize()
  }

  def 'Scenario 2 - Order does not have items'() {
    given: 'the message body and property are initialized'
    def msgBody = this.getClass().getResource('input2.xml').newInputStream()
    exchange.getIn().setBody(msgBody)
    msg.setBody(exchange.getIn().getBody())
    msg.setProperty('DocType', 'HDR')

    when: 'we execute the Groovy script'
    script.processData(msg)
    exchange.getIn().setBody(msg.getBody())

    then: 'the output message body is as expected'
    msg.getBody(String) == this.getClass().getResource('output2.xml').text.normalize()
  }
}