package de.schlaich.gunnar.rhapsody.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;

/** Wandelt ```plantuml```-Blöcke in <img-Tags um. */
class PlantUmlNodeRenderer implements NodeRenderer {

    // Transcoder EINMAL erstellen – thread-safe und schnell
    private static final Transcoder TRANSCODER =
            TranscoderUtil.getDefaultTranscoder();

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        // ohne Set.of() – Java-8/9-Variante
        return Collections.singleton(
            new NodeRenderingHandler<>(FencedCodeBlock.class, this::render));
    }

    private void render(FencedCodeBlock node,
                        NodeRendererContext ctx,
                        HtmlWriter html) {

        String info = node.getInfo().toString();          // fence-info
        if (!info.equals("plantuml") && !info.equals("puml"))
        {   // alles andere normal rendern
            ctx.delegateRender();
            return;
        }

        String src = node.getContentChars().toString();
        
        Path tmp = null;
		try
		{
			tmp = Files.createTempFile("puml_", ".png");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (OutputStream os = Files.newOutputStream(tmp)) {
            try
			{
				new SourceStringReader(src)
				    .outputImage(os, new FileFormatOption(FileFormat.PNG));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        html.raw("<img src=\"" + tmp.toUri() + "\" alt=\"PlantUML\"/>");
        
        /*
        
        SourceStringReader rdr = new SourceStringReader(src);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try
			{
				rdr.outputImage(baos, new FileFormatOption(FileFormat.PNG));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            html.raw("<img src=\"data:image/png;base64," + b64 + "\"/>");
            
            //System.out.println("data:image/png;base64," + b64 + "");
            
            
        }
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		*/
        
        /*
        
        try {
            // Diagrammcode ➜ deflate+Base64 ➜ URL
            String enc = TRANSCODER.encode(src);
            String url = "https://www.plantuml.com/plantuml/png/" + enc;
            
            html.line().raw("<img src=\"" + url + "\" alt=\"PlantUML diagram\"/>").line();
           
            System.out.println("PlantUML-URL: " + url);

        } catch (IOException ex) {
            // Fallback – zeigt Codeblock, wenn Encodieren fehlschlägt
            ctx.delegateRender();
        }
        
        */
        
    }
}
