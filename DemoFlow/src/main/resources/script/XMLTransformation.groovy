import com.sap.gateway.ip.core.customdev.util.Message
import com.sap.it.api.ITApiFactory
import com.sap.it.api.mapping.ValueMappingApi
import groovy.xml.MarkupBuilder

import java.text.SimpleDateFormat

def Message processData(Message message) {
    Reader reader = message.getBody(Reader)
    def Order = new XmlSlurper().parse(reader)
    Writer writer = new StringWriter()
    def builder = new MarkupBuilder(writer)

    def sourceDocType = message.getProperty('DocType')
    ValueMappingApi api = ITApiFactory.getService(ValueMappingApi, null)

    def items = Order.Item.findAll { it.Valid.text() == 'true' }
    builder.PurchaseOrder {
        'Header' {
            'ID' Order.Header.OrderNumber
            'DocumentDate' new SimpleDateFormat('yyyy-MM-dd').format(new SimpleDateFormat('yyyyMMdd').parse(Order.Header.Date.text()))
            if (!items.size())
                'DocumentType' api.getMappedValue('S4', 'DocType', sourceDocType, 'ACME', 'DocumentType')
        }

        items.each { item ->
            'Item' {
                'ItemNumber' item.ItemNumber.text().padLeft(3, '0')
                'ProductCode' item.MaterialNumber
                'Quantity' item.Quantity
            }
        }
    }

    message.setBody(writer.toString())
    return message
}