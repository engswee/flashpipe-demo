import groovy.json.JsonSlurper
import io.github.engswee.flashpipe.cpi.simulation.TestCaseRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class XMLTransformationSimulationIT extends Specification {
    @Shared
    TestCaseRunner testCaseRunner

    def setupSpec() {
        def host = System.getenv('HOST_TMN')
        def user = System.getenv('BASIC_USERID')
        def password = System.getenv('BASIC_PASSWORD')
        testCaseRunner = new TestCaseRunner(host, user, password)
    }

    @Unroll
    def 'Simulation Test: #testCaseName'() {
        when:
        testCaseRunner.run(TestCaseContentFile)
        Map expectedHeaders = testCaseRunner.getExpectedOutputHeaders()
        Map expectedProperties = testCaseRunner.getExpectedOutputProperties()
        String expectedBody = testCaseRunner.getExpectedOutputBody()

        then:
        verifyAll {
            // Headers
            if (expectedHeaders.size() > 0) {
                expectedHeaders.each { k, v ->
                    assert testCaseRunner.getActualOutputHeaders().get(k) == v
                }
            }
            // Properties
            if (expectedProperties.size() > 0) {
                expectedProperties.each { k, v ->
                    assert testCaseRunner.getActualOutputProperties().get(k) == v
                }
            }
            // Body
            if (expectedBody)
                testCaseRunner.getActualOutputBody() == expectedBody
        }

        where:
        TestCaseContentFile         | _
        '/test-case/TestCase1.json' | _
        '/test-case/TestCase2.json' | _

        testCaseName = new JsonSlurper().parse(this.getClass().getResource(TestCaseContentFile)).TestCase.Name
    }
}