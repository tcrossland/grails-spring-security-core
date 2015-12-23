import pages.user.CreateUserPage
import pages.user.ListUserPage
import pages.user.ShowUserPage
import spock.lang.Stepwise

@Stepwise
class BCryptSpec extends AbstractSecuritySpec {

	def 'create a user'() {
		when:
		to ListUserPage
		newUserButton.click()

		then:
		at CreateUserPage

		when:
		username = 'user1'
		password = 'p4ssw0rd'
		$('#enabled').click()
		createButton.click()

		then:
		at ShowUserPage
		username == 'user1'
		userEnabled == true

		when:
		to ListUserPage

		then:
		userRows.size() == 1
	}

	def 'test bcrypt'() {
		when:
		String encryptedPassword = getContent('hack/getUserProperty?user=user1&propName=password')

		then:
		encryptedPassword.startsWith '$2a$'

		when:
		def shaPasswordEncoder = createSha256Encoder()
		String notSalted = shaPasswordEncoder.encodePassword('p4ssw0rd', null)
		String salted = shaPasswordEncoder.encodePassword('p4ssw0rd', 'user1')

		then:
		salted != encryptedPassword
		notSalted != encryptedPassword
	}
}
