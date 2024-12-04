package adapting;

import adapting.mock.GuildMock;
import adapting.mock.JDAMock;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.*;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TypeAdapterTest {

    private static SlashCommandContext context;

    @BeforeAll
    public static void setup() {
        context = TypeAdapterRegistryTest.buildContext(null, "");
    }

    @Test
    public void booleanAdapter_withZero_ShouldReturnFalse() {
        BooleanAdapter adapter = new BooleanAdapter();

        assertFalse(adapter.apply("0", context).orElse(true));
    }

    @Test
    public void booleanAdapter_withFalse_ShouldReturnFalse() {
        BooleanAdapter adapter = new BooleanAdapter();

        assertFalse(adapter.apply("False", context).orElse(true));
        assertFalse(adapter.apply("false", context).orElse(true));
        assertFalse(adapter.apply("fAlSe", context).orElse(true));
    }

    @Test
    public void booleanAdapter_withOne_ShouldReturnTrue() {
        BooleanAdapter adapter = new BooleanAdapter();

        assertTrue(adapter.apply("1", context).orElse(false));
    }

    @Test
    public void booleanAdapter_withTrue_ShouldReturnTrue() {
        BooleanAdapter adapter = new BooleanAdapter();

        assertTrue(adapter.apply("True", context).orElse(false));
        assertTrue(adapter.apply("true", context).orElse(false));
        assertTrue(adapter.apply("tRuE", context).orElse(false));
    }

    @Test
    public void booleanAdapter_withNonBoolean_ShouldBeEmpty() {
        BooleanAdapter adapter = new BooleanAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void byteAdapter_withNumber_ShouldBePresent() {
        ByteAdapter adapter = new ByteAdapter();

        assertEquals(Byte.MIN_VALUE, adapter.apply(String.valueOf(Byte.MIN_VALUE), context).orElseThrow());
        assertEquals(Byte.MAX_VALUE, adapter.apply(String.valueOf(Byte.MAX_VALUE), context).orElseThrow());
    }

    @Test
    public void byteAdapter_withNonNumeric_ShouldBeEmpty() {
        ByteAdapter adapter = new ByteAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void characterAdapter_withOneChar_ShouldBePresent() {
        CharacterAdapter adapter = new CharacterAdapter();

        assertTrue(adapter.apply("c", context).isPresent());
    }

    @Test
    public void characterAdapter_withMultipleChars_ShouldBeEmpty() {
        CharacterAdapter adapter = new CharacterAdapter();

        assertFalse(adapter.apply("chars", context).isPresent());
    }

    @Test
    public void doubleAdapter_withNumber_ShouldReturnDouble() {
        DoubleAdapter adapter = new DoubleAdapter();

        assertEquals(Double.MIN_VALUE, adapter.apply(String.valueOf(Double.MIN_VALUE), context).orElseThrow());
        assertEquals(Double.MIN_EXPONENT, adapter.apply(String.valueOf(Double.MIN_EXPONENT), context).orElseThrow());
        assertEquals(Double.MAX_VALUE, adapter.apply(String.valueOf(Double.MAX_VALUE), context).orElseThrow());
        assertEquals(Double.MAX_EXPONENT, adapter.apply(String.valueOf(Double.MAX_EXPONENT), context).orElseThrow());
    }

    @Test
    public void doubleAdapter_withNonNumeric_ShouldBeEmpty() {
        DoubleAdapter adapter = new DoubleAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void floatAdapter_withNumber_ShouldReturnDouble() {
        FloatAdapter adapter = new FloatAdapter();

        assertEquals(Float.MIN_VALUE, adapter.apply(String.valueOf(Float.MIN_VALUE), context).orElseThrow());
        assertEquals(Float.MIN_EXPONENT, adapter.apply(String.valueOf(Float.MIN_EXPONENT), context).orElseThrow());
        assertEquals(Float.MAX_VALUE, adapter.apply(String.valueOf(Float.MAX_VALUE), context).orElseThrow());
        assertEquals(Float.MAX_EXPONENT, adapter.apply(String.valueOf(Float.MAX_EXPONENT), context).orElseThrow());
    }

    @Test
    public void floatAdapter_withNonNumeric_ShouldBeEmpty() {
        FloatAdapter adapter = new FloatAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void integerAdapter_withNumber_ShouldReturnDouble() {
        IntegerAdapter adapter = new IntegerAdapter();

        assertEquals(Integer.MIN_VALUE, adapter.apply(String.valueOf(Integer.MIN_VALUE), context).orElseThrow());
        assertEquals(Integer.MAX_VALUE, adapter.apply(String.valueOf(Integer.MAX_VALUE), context).orElseThrow());
    }

    @Test
    public void integerAdapter_withNonNumeric_ShouldBeEmpty() {
        IntegerAdapter adapter = new IntegerAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void longAdapter_withNumber_ShouldReturnDouble() {
        LongAdapter adapter = new LongAdapter();

        assertEquals(Long.MIN_VALUE, adapter.apply(String.valueOf(Long.MIN_VALUE), context).orElseThrow());
        assertEquals(Long.MAX_VALUE, adapter.apply(String.valueOf(Long.MAX_VALUE), context).orElseThrow());
    }

    @Test
    public void longAdapter_withNonNumeric_ShouldBeEmpty() {
        LongAdapter adapter = new LongAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void shortAdapter_withNumber_ShouldReturnDouble() {
        ShortAdapter adapter = new ShortAdapter();

        assertEquals(Short.MIN_VALUE, adapter.apply(String.valueOf(Short.MIN_VALUE), context).orElseThrow());
        assertEquals(Short.MAX_VALUE, adapter.apply(String.valueOf(Short.MAX_VALUE), context).orElseThrow());
    }

    @Test
    public void shortAdapter_withNonNumeric_ShouldBeEmpty() {
        ShortAdapter adapter = new ShortAdapter();

        assertFalse(adapter.apply("text", context).isPresent());
    }

    @Test
    public void adapter_withExistingId_ShouldReturnUser() {
        UserAdapter adapter = new UserAdapter();

        assertEquals(JDAMock.USER, adapter.apply(String.valueOf(JDAMock.USER.getIdLong()), context).orElseThrow());
    }

    @Test
    public void adapter_withNonExistingId_ShouldBeEmpty() {
        UserAdapter adapter = new UserAdapter();

        assertFalse(adapter.apply("1234567890", context).isPresent());
    }

    @Test
    public void adapter_withExistingName_ShouldReturnUser() {
        UserAdapter adapter = new UserAdapter();

        assertEquals(JDAMock.USER, adapter.apply(JDAMock.USER.getName(), context).orElseThrow());
    }

    @Test
    public void adapter_withNonExistingName_ShouldBeEmpty() {
        UserAdapter adapter = new UserAdapter();

        assertFalse(adapter.apply("thispersondoesnotexist.com", context).isPresent());
    }

    @Test
    public void memberAdapter_withExistingId_ShouldReturnMember() {
        MemberAdapter adapter = new MemberAdapter();

        assertEquals(GuildMock.MEMBER, adapter.apply(String.valueOf(GuildMock.MEMBER.getIdLong()), context).orElseThrow());
    }

    @Test
    public void memberAdapter_withNonExistingId_ShouldBeEmpty() {
        MemberAdapter adapter = new MemberAdapter();

        assertFalse(adapter.apply("1234567890", context).isPresent());
    }

    @Test
    public void memberAdapter_withExistingName_ShouldReturnMember() {
        MemberAdapter adapter = new MemberAdapter();

        assertEquals(GuildMock.MEMBER, adapter.apply(GuildMock.MEMBER.getNickname(), context).orElseThrow());
    }

    @Test
    public void memberAdapter_withNonExistingName_ShouldBeEmpty() {
        MemberAdapter adapter = new MemberAdapter();

        assertFalse(adapter.apply("thispersondoesnotexist.com", context).isPresent());
    }

    @Test
    public void memberAdapter_inNotGuildContext_ShouldBeEmpty() {
        MemberAdapter adapter = new MemberAdapter();

        assertFalse(adapter.apply("name", context).isPresent());
    }

    @Test
    public void roleAdapter_withExistingId_ShouldReturnMember() {
        RoleAdapter adapter = new RoleAdapter();

        assertEquals(GuildMock.ROLE, adapter.apply(String.valueOf(GuildMock.ROLE.getIdLong()), context).orElseThrow());
    }

    @Test
    public void roleAdapter_withNonExistingId_ShouldBeEmpty() {
        RoleAdapter adapter = new RoleAdapter();

        assertFalse(adapter.apply("1234567890", context).isPresent());
    }

    @Test
    public void roleAdapter_withExistingName_ShouldReturnMember() {
        RoleAdapter adapter = new RoleAdapter();

        assertEquals(GuildMock.ROLE, adapter.apply(GuildMock.ROLE.getName(), context).orElseThrow());
    }

    @Test
    public void roleAdapter_withNonExistingName_ShouldBeEmpty() {
        RoleAdapter adapter = new RoleAdapter();

        assertFalse(adapter.apply("thispersondoesnotexist.com", context).isPresent());
    }

    @Test
    public void roleAdapter_inNotGuildContext_ShouldBeEmpty() {
        RoleAdapter adapter = new RoleAdapter();

        assertFalse(adapter.apply("name", context).isPresent());
    }

    @Test
    public void textChannelAdapter_withExistingId_ShouldReturnMember() {
        TextChannelAdapter adapter = new TextChannelAdapter();

        assertEquals(GuildMock.TEXT_CHANNEL, adapter.apply(String.valueOf(GuildMock.TEXT_CHANNEL.getIdLong()), context).orElseThrow());
    }

    @Test
    public void textChannelAdapter_withNonExistingId_ShouldBeEmpty() {
        TextChannelAdapter adapter = new TextChannelAdapter();

        assertFalse(adapter.apply("1234567890", context).isPresent());
    }

    @Test
    public void textChannelAdapter_withExistingName_ShouldReturnMember() {
        TextChannelAdapter adapter = new TextChannelAdapter();

        assertEquals(GuildMock.TEXT_CHANNEL, adapter.apply(GuildMock.TEXT_CHANNEL.getName(), context).orElseThrow());
    }

    @Test
    public void textChannelAdapter_withNonExistingName_ShouldBeEmpty() {
        TextChannelAdapter adapter = new TextChannelAdapter();

        assertFalse(adapter.apply("thispersondoesnotexist.com", context).isPresent());
    }

    @Test
    public void textChannelAdapter_inNotGuildContext_ShouldBeEmpty() {
        TextChannelAdapter adapter = new TextChannelAdapter();

        assertFalse(adapter.apply("name", context).isPresent());
    }

    @Test
    public void sanitizeMention__() {
        MemberAdapter memberAdapter = new MemberAdapter();

        assertEquals("1234", memberAdapter.sanitizeMention("<@1234>"));
        assertEquals("1234", memberAdapter.sanitizeMention("<@!1234>"));
        assertEquals("1234", memberAdapter.sanitizeMention("<#1234>"));
        assertEquals("1234", memberAdapter.sanitizeMention("<@&1234>"));

        assertEquals("<@1234", memberAdapter.sanitizeMention("<@1234"));
        assertEquals("<@!1234", memberAdapter.sanitizeMention("<@!1234"));
        assertEquals("<#1234", memberAdapter.sanitizeMention("<#1234"));
        assertEquals("<@&1234", memberAdapter.sanitizeMention("<@&1234"));

        assertEquals("<@text>", memberAdapter.sanitizeMention("<@text>"));
        assertEquals("<@!text>", memberAdapter.sanitizeMention("<@!text>"));
        assertEquals("<#text>", memberAdapter.sanitizeMention("<#text>"));
        assertEquals("<@&text>", memberAdapter.sanitizeMention("<@&text>"));

        assertEquals("<1234>", memberAdapter.sanitizeMention("<1234>"));
        assertEquals("<1234", memberAdapter.sanitizeMention("<1234"));
        assertEquals("1234>", memberAdapter.sanitizeMention("1234>"));
    }
}
