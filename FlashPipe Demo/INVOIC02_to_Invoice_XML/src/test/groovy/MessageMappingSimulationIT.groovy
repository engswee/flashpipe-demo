import io.github.engswee.flashpipe.cpi.simulation.Simulator
import io.github.engswee.flashpipe.http.HTTPExecuter
import io.github.engswee.flashpipe.http.HTTPExecuterApacheImpl
import spock.lang.Shared
import spock.lang.Specification

class MessageMappingSimulationIT extends Specification {
    @Shared
    Simulator simulator

    def setupSpec() {
        def host = System.getenv('HOST_TMN')
        def user = System.getenv('BASIC_USERID')
        def password = System.getenv('BASIC_PASSWORD')
        HTTPExecuter httpExecuter = HTTPExecuterApacheImpl.newInstance('https', host, 443, user, password)
        simulator = new Simulator(httpExecuter)
    }

    def 'Test Message mapping'() {
        when:
        Map outputMessage = simulator.simulate(this.class.getResource('/test-data/input.xml').getBytes(), 'INVOIC02_Message_Mapping', 'SequenceFlow_3', 'SequenceFlow_9', 'Process_1', [:], [:])

        then:
        def root = new XmlSlurper().parse(new ByteArrayInputStream(outputMessage.body as byte[]))
        verifyAll {
            root.Header.InvoiceNo.text() == '1234567890'
            root.Header.Currency.text() == 'USD'
            root.Header.PaymentTerms.text() == 'NT30'
            root.Items.size() == 3
            root.Items[0].Quantity.text() == '30.000'
            root.Items[1].Quantity.text() == '15.000'
            root.Items[2].Quantity.text() == '25.000'
        }
    }
}