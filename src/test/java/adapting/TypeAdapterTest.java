//package adapting;
//
//import adapting.mock.GuildMock;
//import adapting.mock.JDAMock;
//import adapting.mock.MessageReceivedEventMock;
//import com.github.kaktushose.jda.commands.dispatching.CommandContext;
//import com.github.kaktushose.jda.commands.dispatching.adapter.impl.*;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TypeAdapterTest {
//
//    private static CommandContext context;
//
//    @BeforeAll
//    public static void setup() {
//        context = new CommandContext();
//    }
//
//    @Test
//    public void booleanAdapter_withZero_ShouldReturnFalse() {
//        BooleanAdapter adapter = new BooleanAdapter();
//
//        assertFalse(adapter.parse("0", context).orElse(true));
//    }
//
//    @Test
//    public void booleanAdapter_withFalse_ShouldReturnFalse() {
//        BooleanAdapter adapter = new BooleanAdapter();
//
//        assertFalse(adapter.parse("False", context).orElse(true));
//        assertFalse(adapter.parse("false", context).orElse(true));
//        assertFalse(adapter.parse("fAlSe", context).orElse(true));
//    }
//
//    @Test
//    public void booleanAdapter_withOne_ShouldReturnTrue() {
//        BooleanAdapter adapter = new BooleanAdapter();
//
//        assertTrue(adapter.parse("1", context).orElse(false));
//    }
//
//    @Test
//    public void booleanAdapter_withTrue_ShouldReturnTrue() {
//        BooleanAdapter adapter = new BooleanAdapter();
//
//        assertTrue(adapter.parse("True", context).orElse(false));
//        assertTrue(adapter.parse("true", context).orElse(false));
//        assertTrue(adapter.parse("tRuE", context).orElse(false));
//    }
//
//    @Test
//    public void booleanAdapter_withNonBoolean_ShouldBeEmpty() {
//        BooleanAdapter adapter = new BooleanAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void byteAdapter_withNumber_ShouldBePresent() {
//        ByteAdapter adapter = new ByteAdapter();
//
//        assertEquals(Byte.MIN_VALUE, adapter.parse(String.valueOf(Byte.MIN_VALUE), context).orElse(null));
//        assertEquals(Byte.MAX_VALUE, adapter.parse(String.valueOf(Byte.MAX_VALUE), context).orElse(null));
//    }
//
//    @Test
//    public void byteAdapter_withNonNumeric_ShouldBeEmpty() {
//        ByteAdapter adapter = new ByteAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void characterAdapter_withOneChar_ShouldBePresent() {
//        CharacterAdapter adapter = new CharacterAdapter();
//
//        assertTrue(adapter.parse("c", context).isPresent());
//    }
//
//    @Test
//    public void characterAdapter_withMultipleChars_ShouldBeEmpty() {
//        CharacterAdapter adapter = new CharacterAdapter();
//
//        assertFalse(adapter.parse("chars", context).isPresent());
//    }
//
//    @Test
//    public void doubleAdapter_withNumber_ShouldReturnDouble() {
//        DoubleAdapter adapter = new DoubleAdapter();
//
//        assertEquals(Double.MIN_VALUE, adapter.parse(String.valueOf(Double.MIN_VALUE), context).orElse(null));
//        assertEquals(Double.MIN_EXPONENT, adapter.parse(String.valueOf(Double.MIN_EXPONENT), context).orElse(null));
//        assertEquals(Double.MAX_VALUE, adapter.parse(String.valueOf(Double.MAX_VALUE), context).orElse(null));
//        assertEquals(Double.MAX_EXPONENT, adapter.parse(String.valueOf(Double.MAX_EXPONENT), context).orElse(null));
//    }
//
//    @Test
//    public void doubleAdapter_withNonNumeric_ShouldBeEmpty() {
//        DoubleAdapter adapter = new DoubleAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void floatAdapter_withNumber_ShouldReturnDouble() {
//        FloatAdapter adapter = new FloatAdapter();
//
//        assertEquals(Float.MIN_VALUE, adapter.parse(String.valueOf(Float.MIN_VALUE), context).orElse(null));
//        assertEquals(Float.MIN_EXPONENT, adapter.parse(String.valueOf(Float.MIN_EXPONENT), context).orElse(null));
//        assertEquals(Float.MAX_VALUE, adapter.parse(String.valueOf(Float.MAX_VALUE), context).orElse(null));
//        assertEquals(Float.MAX_EXPONENT, adapter.parse(String.valueOf(Float.MAX_EXPONENT), context).orElse(null));
//    }
//
//    @Test
//    public void floatAdapter_withNonNumeric_ShouldBeEmpty() {
//        FloatAdapter adapter = new FloatAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void integerAdapter_withNumber_ShouldReturnDouble() {
//        IntegerAdapter adapter = new IntegerAdapter();
//
//        assertEquals(Integer.MIN_VALUE, adapter.parse(String.valueOf(Integer.MIN_VALUE), context).orElse(null));
//        assertEquals(Integer.MAX_VALUE, adapter.parse(String.valueOf(Integer.MAX_VALUE), context).orElse(null));
//    }
//
//    @Test
//    public void integerAdapter_withNonNumeric_ShouldBeEmpty() {
//        IntegerAdapter adapter = new IntegerAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void longAdapter_withNumber_ShouldReturnDouble() {
//        LongAdapter adapter = new LongAdapter();
//
//        assertEquals(Long.MIN_VALUE, adapter.parse(String.valueOf(Long.MIN_VALUE), context).orElse(null));
//        assertEquals(Long.MAX_VALUE, adapter.parse(String.valueOf(Long.MAX_VALUE), context).orElse(null));
//    }
//
//    @Test
//    public void longAdapter_withNonNumeric_ShouldBeEmpty() {
//        LongAdapter adapter = new LongAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void shortAdapter_withNumber_ShouldReturnDouble() {
//        ShortAdapter adapter = new ShortAdapter();
//
//        assertEquals(Short.MIN_VALUE, adapter.parse(String.valueOf(Short.MIN_VALUE), context).orElse(null));
//        assertEquals(Short.MAX_VALUE, adapter.parse(String.valueOf(Short.MAX_VALUE), context).orElse(null));
//    }
//
//    @Test
//    public void shortAdapter_withNonNumeric_ShouldBeEmpty() {
//        ShortAdapter adapter = new ShortAdapter();
//
//        assertFalse(adapter.parse("text", context).isPresent());
//    }
//
//    @Test
//    public void userAdapter_withExistingId_ShouldReturnUser() {
//        UserAdapter userAdapter = new UserAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(JDAMock.USER, userAdapter.parse(String.valueOf(JDAMock.USER.getIdLong()), context).orElse(null));
//    }
//
//    @Test
//    public void userAdapter_withNonExistingId_ShouldBeEmpty() {
//        UserAdapter userAdapter = new UserAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("1234567890", context).isPresent());
//    }
//
//    @Test
//    public void userAdapter_withExistingName_ShouldReturnUser() {
//        UserAdapter userAdapter = new UserAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(JDAMock.USER, userAdapter.parse(JDAMock.USER.getName(), context).orElse(null));
//    }
//
//    @Test
//    public void userAdapter_withNonExistingName_ShouldBeEmpty() {
//        UserAdapter userAdapter = new UserAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("thispersondoesnotexist.com", context).isPresent());
//    }
//
//    @Test
//    public void memberAdapter_withExistingId_ShouldReturnMember() {
//        MemberAdapter userAdapter = new MemberAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(GuildMock.MEMBER, userAdapter.parse(String.valueOf(GuildMock.MEMBER.getIdLong()), context).orElse(null));
//    }
//
//    @Test
//    public void memberAdapter_withNonExistingId_ShouldBeEmpty() {
//        MemberAdapter userAdapter = new MemberAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("1234567890", context).isPresent());
//    }
//
//    @Test
//    public void memberAdapter_withExistingName_ShouldReturnMember() {
//        MemberAdapter userAdapter = new MemberAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(GuildMock.MEMBER, userAdapter.parse(GuildMock.MEMBER.getNickname(), context).orElse(null));
//    }
//
//    @Test
//    public void memberAdapter_withNonExistingName_ShouldBeEmpty() {
//        MemberAdapter userAdapter = new MemberAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("thispersondoesnotexist.com", context).isPresent());
//    }
//
//    @Test
//    public void memberAdapter_inNotGuildContext_ShouldBeEmpty() {
//        MemberAdapter userAdapter = new MemberAdapter();
//        context.setEvent(new MessageReceivedEventMock(false));
//
//        assertFalse(userAdapter.parse("name", context).isPresent());
//    }
//
//    @Test
//    public void roleAdapter_withExistingId_ShouldReturnMember() {
//        RoleAdapter userAdapter = new RoleAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(GuildMock.ROLE, userAdapter.parse(String.valueOf(GuildMock.ROLE.getIdLong()), context).orElse(null));
//    }
//
//    @Test
//    public void roleAdapter_withNonExistingId_ShouldBeEmpty() {
//        RoleAdapter userAdapter = new RoleAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("1234567890", context).isPresent());
//    }
//
//    @Test
//    public void roleAdapter_withExistingName_ShouldReturnMember() {
//        RoleAdapter userAdapter = new RoleAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(GuildMock.ROLE, userAdapter.parse(GuildMock.ROLE.getName(), context).orElse(null));
//    }
//
//    @Test
//    public void roleAdapter_withNonExistingName_ShouldBeEmpty() {
//        RoleAdapter userAdapter = new RoleAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("thispersondoesnotexist.com", context).isPresent());
//    }
//
//    @Test
//    public void roleAdapter_inNotGuildContext_ShouldBeEmpty() {
//        RoleAdapter userAdapter = new RoleAdapter();
//        context.setEvent(new MessageReceivedEventMock(false));
//
//        assertFalse(userAdapter.parse("name", context).isPresent());
//    }
//
//    @Test
//    public void textChannelAdapter_withExistingId_ShouldReturnMember() {
//        TextChannelAdapter userAdapter = new TextChannelAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(GuildMock.TEXT_CHANNEL, userAdapter.parse(String.valueOf(GuildMock.TEXT_CHANNEL.getIdLong()), context).orElse(null));
//    }
//
//    @Test
//    public void textChannelAdapter_withNonExistingId_ShouldBeEmpty() {
//        TextChannelAdapter userAdapter = new TextChannelAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("1234567890", context).isPresent());
//    }
//
//    @Test
//    public void textChannelAdapter_withExistingName_ShouldReturnMember() {
//        TextChannelAdapter userAdapter = new TextChannelAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertEquals(GuildMock.TEXT_CHANNEL, userAdapter.parse(GuildMock.TEXT_CHANNEL.getName(), context).orElse(null));
//    }
//
//    @Test
//    public void textChannelAdapter_withNonExistingName_ShouldBeEmpty() {
//        TextChannelAdapter userAdapter = new TextChannelAdapter();
//        context.setEvent(new MessageReceivedEventMock(true));
//
//        assertFalse(userAdapter.parse("thispersondoesnotexist.com", context).isPresent());
//    }
//
//    @Test
//    public void textChannelAdapter_inNotGuildContext_ShouldBeEmpty() {
//        TextChannelAdapter userAdapter = new TextChannelAdapter();
//        context.setEvent(new MessageReceivedEventMock(false));
//
//        assertFalse(userAdapter.parse("name", context).isPresent());
//    }
//
//    @Test
//    public void sanitizeMention__() {
//        MemberAdapter memberAdapter = new MemberAdapter();
//
//        assertEquals("1234", memberAdapter.sanitizeMention("<@1234>"));
//        assertEquals("1234", memberAdapter.sanitizeMention("<@!1234>"));
//        assertEquals("1234", memberAdapter.sanitizeMention("<#1234>"));
//        assertEquals("1234", memberAdapter.sanitizeMention("<@&1234>"));
//
//        assertEquals("<@1234", memberAdapter.sanitizeMention("<@1234"));
//        assertEquals("<@!1234", memberAdapter.sanitizeMention("<@!1234"));
//        assertEquals("<#1234", memberAdapter.sanitizeMention("<#1234"));
//        assertEquals("<@&1234", memberAdapter.sanitizeMention("<@&1234"));
//
//        assertEquals("<@text>", memberAdapter.sanitizeMention("<@text>"));
//        assertEquals("<@!text>", memberAdapter.sanitizeMention("<@!text>"));
//        assertEquals("<#text>", memberAdapter.sanitizeMention("<#text>"));
//        assertEquals("<@&text>", memberAdapter.sanitizeMention("<@&text>"));
//
//        assertEquals("<1234>", memberAdapter.sanitizeMention("<1234>"));
//        assertEquals("<1234", memberAdapter.sanitizeMention("<1234"));
//        assertEquals("1234>", memberAdapter.sanitizeMention("1234>"));
//    }
//}
