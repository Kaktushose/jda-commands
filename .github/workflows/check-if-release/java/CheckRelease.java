//COMPILE_OPTIONS --enable-preview --release 24
//RUNTIME_OPTIONS --enable-preview

//JAVA 24

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final static String REGEX = "^Release *" + // prefix
        "((0|[1-9]\\d*)" + // major
        "(?:[.](0|[1-9]\\d*))?" + // minor (optional)
        "(?:[.](0|[1-9]\\d*))?" + // patch (optional)
        "(?:-(0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:[.](?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*)?" + // prerelease (optional)
        "(?:[+]([0-9a-zA-Z-]+(?:[.][0-9a-zA-Z-]+)*))?) *" + // build metadata (optional)
        "(?:: *(.+))?$"; // message

void main() throws IOException {
    String msg = System.getenv("MSG");
    Matcher matcher = Pattern.compile(REGEX).matcher(msg);

    Map<String, String> outputs = new HashMap<>();

    boolean matches = matcher.matches();
    outputs.put("release", String.valueOf(matches));

    if (matches) {
        outputs.put("version", matcher.group(1));
        outputs.put("version_major", matcher.group(2));
        outputs.put("version_minor", orDefault(matcher.group(3), "0"));
        outputs.put("title", matcher.group(7));
    }

    String textToWrite = outputs.entrySet()
            .stream()
            .map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(System.lineSeparator()));

    Files.writeString(Path.of(System.getenv("GITHUB_OUTPUT")), textToWrite, StandardOpenOption.APPEND);
}

String orDefault(String result, String fallback) {
    return result == null
            ? fallback
            : result;
}