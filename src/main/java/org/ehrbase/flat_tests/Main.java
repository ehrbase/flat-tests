/**
 * 
 */
package org.ehrbase.flat_tests;

import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import org.openehr.am.aom.Template;
import org.openehr.rm.composition.Composition;

import com.google.common.collect.ImmutableList;

import care.better.platform.jaxb.JaxbRegistry;
import care.better.platform.web.template.WebTemplate;
import care.better.platform.web.template.builder.WebTemplateBuilder;
import care.better.platform.web.template.converter.FromRawConversion;
import care.better.platform.web.template.converter.flat.RawToFlatConverter;
import care.better.platform.web.template.builder.context.WebTemplateBuilderContext;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Pablo Pazos <pablo.pazos@cabolabs.com>
 *
 */
public class Main
{

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      try
      {
         // read and parse compo
         String compositionFile = "minimal_evaluation.en.v1_20210927084720_000001_1.xml";
         
         Unmarshaller unmarshaller = JaxbRegistry.getInstance().getUnmarshaller();
         
         ClassLoader classLoader = Main.class.getClassLoader();
         
         if (classLoader == null)
         {
            System.err.println("classLoader is null");
         }
         
         InputStream inputStream = classLoader.getResourceAsStream(compositionFile);
         
         if (inputStream == null)
         {
            System.err.println("Composition resource was not found: "+ compositionFile);
            //throw new RuntimeException("Composition resource was not found: "+ compositionFile);
         }
         
         
         Composition compo = unmarshaller.unmarshal(new StreamSource(inputStream), Composition.class).getValue();
         
         
         // read and parse template
         String optFile = "minimal_evaluation.opt";
         inputStream = classLoader.getResourceAsStream(optFile);
         
         if (inputStream == null)
         {
            System.err.println("OPT resource was not found: "+ optFile);
            throw new RuntimeException("OPT resource was not found: "+ optFile);
         }
         
         Template template = unmarshaller.unmarshal(new StreamSource(inputStream), Template.class).getValue();
         WebTemplate webTemplate = WebTemplateBuilder.buildNonNull(template, new WebTemplateBuilderContext("en", ImmutableList.of("en")));
   
         
         Map<String, Object> flat = new RawToFlatConverter().convert(webTemplate, FromRawConversion.create(), compo);
         
         for (Map.Entry<String, Object> e: flat.entrySet())
         {
            System.out.println(e.getKey() +": "+ e.getValue().toString());
         }
         
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.out.println(e.getMessage());
         System.out.println(e.getCause().getMessage());
      }

   }

}
