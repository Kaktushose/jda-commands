import logging

def on_page_markdown(markdown, page, config, files):
    log = logging.getLogger("mkdocs")
    log.info(f"Parsing: {page.file.src_path}")
    return markdown
