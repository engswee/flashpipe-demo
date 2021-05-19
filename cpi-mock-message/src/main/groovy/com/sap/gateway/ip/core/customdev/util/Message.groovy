package com.sap.gateway.ip.core.customdev.util

import org.apache.camel.Exchange
import org.apache.camel.TypeConversionException

class Message {

  Exchange exchange
  Object msgBody
  Map<String, Object> msgHeaders
  Map<String, Object> msgProps

  Message(Exchange exchange) {
    this.exchange = exchange
  }

  public <Klass> Klass getBody(Class<Klass> klass)
          throws TypeConversionException {
    def body = this.exchange.getIn().getBody(klass)
    return body ?: null
  }

  Object getBody() {
    return this.msgBody
  }

  void setBody(Object msgBody) {
    this.msgBody = msgBody
  }

  Map<String, Object> getHeaders() {
    return this.msgHeaders
  }

  public <Klass> Klass getHeader(String name, Class<Klass> klass)
          throws TypeConversionException {
    if (!this.exchange.getIn().getHeader(name)) {
      return null
    } else {
      def header = this.exchange.getIn().getHeader(name, klass)
      return header ?: null
    }
  }

  void setHeaders(Map<String, Object> msgHeaders) {
    this.msgHeaders = msgHeaders
  }

  void setHeader(String name, Object value) {
    if (!this.msgHeaders)
      this.msgHeaders = [:]
    this.msgHeaders.put(name, value)
  }

  Map<String, Object> getProperties() {
    return this.msgProps
  }

  void setProperties(Map<String, Object> msgProps) {
    this.msgProps = msgProps
  }

  void setProperty(String name, Object value) {
    if (!this.msgProps)
      this.msgProps = [:]
    this.msgProps.put(name, value)
  }

  Object getProperty(String name) {
    return (this.msgProps) ? this.msgProps.get(name) : null
  }
}