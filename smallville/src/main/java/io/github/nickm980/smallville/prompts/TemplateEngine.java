package io.github.nickm980.smallville.prompts;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class TemplateEngine {    
    public String format(String template, Map<String, Object> data) {
	MustacheFactory mf = new DefaultMustacheFactory();
	
	Mustache mustache = mf.compile(new StringReader(template), template);
	StringWriter writer = new StringWriter();
	mustache.execute(writer, data);
	return writer.toString().replaceAll("&#10;", "\n").trim();
    }
}
