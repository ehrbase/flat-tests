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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import care.better.platform.json.jackson.better.BetterObjectMapper;

/**
 * @author Pablo Pazos <pablo.pazos@cabolabs.com>
 *
 */
public class Main
{
   static public File checkFile(String path)
   {
      File file = new File(path);

      if (!file.exists())
      {
         System.out.println("File "+ path +" doesn't exist");
         try {
            System.out.println("File "+ file.getCanonicalPath() +" doesn't exist");
         } catch (Exception e) {
            System.out.println(e.getMessage());
         }
         System.exit(1);
      }
      
      if (!file.canRead())
      {
         System.out.println("File "+ path +" foudn but can't be read, check permissions");
         System.exit(1);
      }

      return file;
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      String compo_path = System.getProperty("compo");
      String opt_path   = System.getProperty("opt");

      if (compo_path == null)
      {
         System.out.println("Usage: $ java -Dcompo=path_to_xml_compo -Dopt=path_to_opt -cp target/flat-tests-0.0.1-jar-with-dependencies.jar org.ehrbase.flat_tests.Main");
         System.out.println("-Dcompo=path_to_xml_compo is missing");
         System.exit(1);
      }

      if (opt_path == null)
      {
         System.out.println("Usage: $ java -Dcompo=path_to_xml_compo -Dopt=path_to_opt -cp target/flat-tests-0.0.1-jar-with-dependencies.jar org.ehrbase.flat_tests.Main");
         System.out.println("-Dopt=path_to_opt is missing");
         System.exit(1);
      }

      File compo_file = checkFile(compo_path);
      File opt_file   = checkFile(opt_path);

      try
      {
         /* test read file from resources
         // read and parse compo
         String compositionFile = "minimal_evaluation.en.v1_20210927084720_000001_1.xml";
         
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
         */

         FileInputStream inputStream = new FileInputStream(compo_file);
         
         Unmarshaller unmarshaller = JaxbRegistry.getInstance().getUnmarshaller();
         Composition compo = unmarshaller.unmarshal(new StreamSource(inputStream), Composition.class).getValue();
         
         
         /*
         // read and parse template
         String optFile = "minimal_evaluation.opt";
         inputStream = classLoader.getResourceAsStream(optFile);
         
         if (inputStream == null)
         {
            System.err.println("OPT resource was not found: "+ optFile);
            throw new RuntimeException("OPT resource was not found: "+ optFile);
         }
         */

         inputStream = new FileInputStream(opt_file);
         
         Template template = unmarshaller.unmarshal(new StreamSource(inputStream), Template.class).getValue();
         WebTemplate webTemplate = WebTemplateBuilder.buildNonNull(template, new WebTemplateBuilderContext("en", ImmutableList.of("en")));
   
         
         Map<String, Object> flat = new RawToFlatConverter().convert(webTemplate, FromRawConversion.create(), compo);
         
         /*
         for (Map.Entry<String, Object> e: flat.entrySet())
         {
            System.out.println(e.getKey() +": "+ e.getValue().toString());
         }
         */

         // this outputs the json in a pretty format, without the writerWithDefaultPrettyPrinter() it is minified json.
         BetterObjectMapper mapper = new BetterObjectMapper();
         mapper.registerModule(new JodaModule());
         mapper.registerModule(new JavaTimeModule());
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         String prettyFormattedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flat);

         //System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flat));

         BufferedWriter writer = new BufferedWriter(new FileWriter(compo_file.getAbsolutePath() + ".flat.json")); // System.getProperty("file.separator") + 
         writer.write(prettyFormattedJson);
         writer.close();

         System.out.println("Flat file generated: "+ compo_file.getAbsolutePath() + ".flat.json");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.out.println(e.getMessage());
         System.out.println(e.getCause().getMessage());
      }

   }

}
