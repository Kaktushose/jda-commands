//COMPILE_OPTIONS --enable-preview --release 24
//RUNTIME_OPTIONS --enable-preview

//JAVA 24

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final static String REGEX = "^Release (0|[1-9]\\d*)(?:[.](0|[1-9]\\d*))?(?:[.](0|[1-9]\\d*))?(?:-(0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*)?(?:\\+([0-9a-zA-Z-]+(?:[.][0-9a-zA-Z-]+)*))? *(?:: *(.+))?$";

void main() {
    String msg = System.getenv("MSG");
    Matcher matcher = Pattern.compile(REGEX).matcher(msg);

    System.out.println(System.getenv("GITHUB_OUTPUT"));
}