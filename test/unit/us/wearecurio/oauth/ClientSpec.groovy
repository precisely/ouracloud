package us.wearecurio.oauth

import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@Mock(Client)
@TestMixin(GrailsUnitTestMixin)
class ClientSpec extends Specification {

    void "test client environment enum for unique id"() {
        expect: "All values of ClientEnvironment should have unique id"
        ClientEnvironment.values()*.id.unique() == ClientEnvironment.values()*.id
    }

    void "test get current for client environment enum"() {
        expect: "Following should be true"
        ClientEnvironment.current == ClientEnvironment.TEST
    }
}
