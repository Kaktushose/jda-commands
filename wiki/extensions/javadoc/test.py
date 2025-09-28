## review note: just let this file here for future testing
import markdown
from javadoc_extension import JavaDocExtension

txt = """
[some link](Event)
[some link](ReplyableEvent#deferReply(boolean))

[String](String)
[GenericInteractionCreateEvent](GenericInteractionCreateEvent)

[Hi](https://jdac.goldmensch.dev)
"""

urls = [
    'https://kaktushose.github.io/jda-commands/javadocs/4/',
    'https://docs.jda.wiki/',
    'https://docs.oracle.com/en/java/javase/24/docs/api/'

]

result = markdown.markdown(txt, extensions=[JavaDocExtension(urls=urls)])
print(result)
