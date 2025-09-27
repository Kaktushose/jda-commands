package messages;

import com.github.kaktushose.jda.commands.message.variables.VariableResolver;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.ProteusBuilder;
import io.github.kaktushose.proteus.mapping.Mapper;
import io.github.kaktushose.proteus.mapping.MappingResult;
import io.github.kaktushose.proteus.type.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class VariableResolverTest {

    @Test
    void variable_not_found() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "{ $not_found }";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("null", resolved);
    }

    @Test
    void found_variable() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "{ $var }";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("this is a variable", resolved);
    }

    @Test
    void found_variable_proteus() {
        record Data(String value) {}
        Proteus.global().register(Type.of(Data.class), Type.of(String.class), Mapper.uni((data, _) -> MappingResult.lossless(data.value)), ProteusBuilder.ConflictStrategy.OVERRIDE);

        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12,
                "data", new Data("my data")
        );

        String text = "{ $data }";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("my data", resolved);
    }

    @Test
    void variable_without_dollar() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "{ var }";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("this is a variable", resolved);
    }

    @Test
    void variable_int() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "{ number }";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("12", resolved);
    }

    @Test
    void variable_leading_text() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "My number: { number }";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("My number: 12", resolved);
    }

    @Test
    void variable_trailing_text() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "{ number } was my number";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("12 was my number", resolved);
    }

    @Test
    void variable_embedded() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "My number { number } is cool";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("My number 12 is cool", resolved);
    }

    @Test
    void escaped_variable() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "Escaped: \\{ number } }}fad}} adf}}";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("Escaped: { number } }}fad}} adf}}", resolved);
    }

    @Test
    void no_closing_bracket() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = "{ number ";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("{ number ", resolved);
    }

    @Test
    void multiline_valid_variable() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                { number 
                
                }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("12", resolved);
    }

    @Test
    void illegal_newline() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                { line 
                break
                }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals(text, resolved);
    }

    @Test
    void illegal_whitespace() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                { white space }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals(text, resolved);
    }

    @Test
    void illegal_dollar() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                { do$llar }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals(text, resolved);
    }

    @Test
    void illegal_bracket() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                { bra{ }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals(text, resolved);
    }

    @Test
    void blank_reference() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                empty: {  }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals(text, resolved);
    }

    @Test
    void some_backslash() {
        Map<String, Object> variables = Map.of(
                "var", "this is a variable",
                "number", 12
        );

        String text = """
                \\\\\\ \\ some backslash \\
                
                \\ { number 
                
                }""";
        String resolved = VariableResolver.resolve(text, variables);
        Assertions.assertEquals("""
                \\\\\\ \\ some backslash \\
                
                \\ 12""", resolved);
    }






}
