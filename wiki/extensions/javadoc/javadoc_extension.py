import json
import re
import urllib.request

from markdown.extensions import Extension
from markdown.treeprocessors import Treeprocessor

# We have to cache them here, because mkdocs is loading markdown extensions multiple times
_index_cache = {}

def read_url(url):
    if not url.startswith("https://"):
        raise ValueError("Only https:// URLs are allowed")

    return urllib.request.urlopen(url).read()

class Index:
    def __init__(self, urls):
        self.i_member = dict()
        self.i_pkg = dict()
        self.urls = tuple(urls)

        if self.urls in _index_cache:
            self.i_member, self.i_pkg = _index_cache[self.urls]
            return

        self.load_members()
        self.load_packages()

        _index_cache[self.urls] = (self.i_member, self.i_pkg)

    def load_members(self):
        for url in self.urls:
            text = read_url(url + '/member-search-index.js')
            json_text = text.removeprefix(b'memberSearchIndex = ').removesuffix(b';updateSearchResults();').strip()
            data = json.loads(json_text)

            index = dict()
            for e in data:
                i = e['c']
                if i not in index:
                    index[i] = list()
                index[i].append(e)

            self.i_member[url] = index

    def load_packages(self):
        for url in self.urls:
            text = read_url(url + '/package-search-index.js')
            json_text = text.removeprefix(b'packageSearchIndex = ').removesuffix(b';updateSearchResults();').strip()
            data = json.loads(json_text)

            index = dict()
            for e in data:
                index[e['l']] = e

            self.i_pkg[url] = index

    def module_name(self, url, pkg):
        e = self.i_pkg[url][pkg]
        if e['l'] == pkg and 'm' in e:
            return e['m']
        return None

    def url(self, pkg, klass, method):
        for e_url in self.i_member.items():
            classes = e_url[1].get(klass)
            if classes is None: continue
            for e in classes:
                if pkg is not None and (e['p'] != pkg): continue
                if method is not None and (e['l'] != method or ('u' in e and e['u'] != method)): continue
                if e['c'] == klass:
                        return self.build_url(e_url[0], e, method is not None)
        return None

    def build_url(self, base, e, include_method):
        pkg: str = e['p']
        module = self.module_name(base, pkg)
        if module is not None: base = base + module + '/'
        base = base + pkg.replace('.', '/') + '/'
        base = base + e['c']

        base = base + '.html'

        if include_method:
            base = base + '#' + (e['u'] if 'u' in e else e['l'])

        return base


"""
regex will match: java.util.com.MyClass#foo(String,int,boolean) -->
    group 1: package (optional)
    group 2: class name
    group 3: method name + parameters (optional)
"""
class JavaDocProcessor(Treeprocessor):
    def __init__(self, md, urls):
        super().__init__(md)

        self.index = Index(urls)

    def run(self, root):
        pattern = re.compile(r'([\w.]*\.)?(\w+)(?:#(\w+\(.*\)))?$')
        for el in root.iter('a'):
            href = el.get('href', '')
            m = pattern.match(href)
            if m:
                url = self.index.url(m.group(1), m.group(2), m.group(3))

                final_text = 'Invalid' if url is None else url
                el.set('href', final_text)


class JavaDocExtension(Extension):
    def __init__(self, **kwargs):
        self.config = {
            'urls': ['', 'A list of javadoc sites to search in.']
        }

        super().__init__(**kwargs)

    def extendMarkdown(self, md):
        md.treeprocessors.register(JavaDocProcessor(md, self.getConfig("urls")), 'javadoc_link_processor', 15)

def makeExtension(**kwargs):
    return JavaDocExtension(**kwargs)
