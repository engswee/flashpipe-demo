// This is Groovy Flowstep Version 2.x, running with Groovy runtime 4, Downgrade the script if older behaviour needed.

import com.sap.it.script.v2.api.Message
import com.sap.it.api.ITApiFactory
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import groovy.json.JsonOutput

Message processData(Message message) {
    def clientCredentialsSecureMaterialName = message.getProperty('ClientCredentialsSecureMaterialName')
    def secureStorageService = ITApiFactory.getService(SecureStoreService, null)
    UserCredential userCredential = secureStorageService.getUserCredential(clientCredentialsSecureMaterialName)

    def clientId = userCredential.getUsername()
    def clientSecret = userCredential.getPassword().toString()

    Map output = ['clientId': clientId, 'clientSecret': clientSecret]

    message.setBody(JsonOutput.toJson(output))
    message.setHeader('Content-Type', 'application/json')
    return message
}