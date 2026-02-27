import com.sap.it.script.v2.api.Message
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.securestore.exception.SecureStoreException
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class SetAuthorizationPayloadSpec extends Specification {
    @Shared
    Script script

    Message msg

    def setupSpec() {
        GroovyShell shell = new GroovyShell()
        script = shell.parse(this.getClass().getResource('/script/v2/SetAuthorizationPayload.groovy').toURI())
    }

    def setup() {
        msg = new Message()
    }

    def 'Scenario 1 - Credential exists'() {
        given:
        msg.setProperty('ClientCredentialsSecureMaterialName', 'Sample-Client-Credentials')
        SecureStoreService secureStoreService = SecureStoreService.getInstance()
        secureStoreService.addCredential('Sample-Client-Credentials', new UserCredential('dummyId', 'dummySecret'))

        when:
        'Script is executed'
        script.processData(msg)

        then:
        def root = new JsonSlurper().parse(msg.getBody(Reader))
        verifyAll {
            root.clientId == 'dummyId'
            root.clientSecret == 'dummySecret'
        }
    }

    def 'Scenario 2 - Credential missing'() {
        given:
        msg.setProperty('ClientCredentialsSecureMaterialName', 'Missing-Client-Credentials')

        when:
        'Script is executed'
        script.processData(msg)

        then:
        def err = thrown(SecureStoreException)
        err.message == 'Could not fetch the credential for alias Missing-Client-Credentials'
    }
}