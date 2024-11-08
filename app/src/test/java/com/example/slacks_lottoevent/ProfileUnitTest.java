import static org.mockito.Mockito.mock;

import android.content.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileUnitTest {
    private Profile profile;
    private Context context;

    @BeforeEach
    public void setup() {
        context = mock(Context.class); // Mock the Context
        profile = new Profile("Tate McRae", "7804448883", "tateMcRae@gmail.com", context);
    }

    @Test
    public void testSetName() {
        profile.setName("Taylor Swift", context);
        assertEquals("Taylor Swift", profile.getName());
    }

    @Test
    public void testSetPhone() {
        profile.setPhone("5878895544");
        assertEquals("5878895544", profile.getPhone());
    }

    @Test
    public void testSetEmail() {
        profile.setEmail("taylorSwift@ualberta.ca");
        assertEquals("taylorSwift@ualberta.ca", profile.getEmail());
    }
}
