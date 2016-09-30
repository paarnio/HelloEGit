/*
 * TÄSTÄ EI TUU TOLKKUA???
 * TOIMII:
 * Importattavaan malliin lisätään luokka (imprt.createClass("x-model://a/class"),
 * 
 * MUTTA MITEN???
 * KO luokka löytyy myös pää-mallin kautta (model.contains(clazz, RDF.type, OWL.Class), 
 * JO ENNEN kuin model.addSubModel(imprt, true) ???
 * JOHTUUKO noista documentManager-modelSpec-olioista ?? 
 * 
 * 
 * Copied from "\javalab\eclwork\2014_Luna\SpinExamplesLib133AsSrc\src\siima   utils\tests
 * From: http://stackoverflow.com/questions/17292675/read-only-file-instances-of-an-ontology-model-in-jena
 * 
 */

package siima.tests;

//import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

//import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class MyBicycleWithImports {

	//@Test
	public void test() {
		
		/* --- Main ontology --- */
		String ont_folder = "data/models/importing_models"; 
		
		String main_ont_file= "bicycle.ttl"; 
		String main_ont_url="http://siima.net/ont/bicycle"; 
		
		//altlocs.add(ont_folder + "/" + main_ont_file);
		//urls.add(main_ont_url);
		
		/* --- Imported ontologies --- */		
		String imp_ont_file="accessories.ttl"; 
		String imp_ont_url= "http://siima.net/ont/accessories"; 
		
		//altlocs.add(ont_folder + "/" + imp_ont_file);
		//urls.add(imp_ont_url);
		
		/*****************
		 * 
		 *  OntDocumentManager & OntModelSpec
		 * 
		 */
		
		
		// Create a doc manager / modelspec to resolve imports
	    final OntDocumentManager docManager = new OntDocumentManager();
	    final OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
	  //VPA: KOE set reasoner
	    Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
	    modelSpec.setReasoner(reasoner);
	    //
	    modelSpec.setDocumentManager(docManager);
	    
	    /* **************
	     * 
	     *  imported Model
	     * 
	     */

	    // Create an imported model that can be referenced by the docManager
	    final OntModel imprt = ModelFactory.createOntologyModel(modelSpec);
	    
	    //docManager.addModel("x-model://a", imprt);
	    docManager.addModel(imp_ont_url, imprt);
	    
	    // Populate imported model with some content
	    //final Ontology imprtOnt = imprt.createOntology("x-model://a");
	    final OntClass clazz = imprt.createClass("x-model://a/class");
	    //assertTrue("delcared class should exist in a", imprt.contains(clazz, RDF.type, OWL.Class));
	    Boolean is_in_model = imprt.contains(clazz, RDF.type, OWL.Class);
	    System.out.println("(1) QUERY: x-model://a/class is in imprt model. Is't it? " + is_in_model);
	    System.out.println("----");
	    //VPA: from:http://answers.semanticweb.com/questions/14955/read-a-turtle-file-into-an-ontmodel
	    InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(ont_folder+"/"+imp_ont_file));
		
	    imprt.read(is,imp_ont_url, "TURTLE");
	    
	    System.out.println("x-model://a before (Import Model)");
	    System.out.println("-------------------------------------------");
	    imprt.write(System.out, "N3");
	    System.out.println("\n");
	    
	    
	    /* **************
	     * 
	     *  Main Model
	     * 
	     */
	    
	    
	    // Create a model which imports x-model://a
	    final OntModel model = ModelFactory.createOntologyModel(modelSpec);
	    model.setDynamicImports(true); //VPA: OPTION?
	   // final Ontology modelOnt = model.createOntology("x-model://b");
	   // modelOnt.addImport(imprtOnt);
	    
	    is = new BufferedInputStream(new FileInputStream(ont_folder+"/"+main_ont_file));		
	    model.read(is,main_ont_url, "TURTLE");
	    
	    System.out.println("x-model://b before (Main Model Before import)");
	    System.out.println("-------------------------------------------");
	    //model.write(System.out, "N3");
	    System.out.println("\n");	    
	    
	    
	    is_in_model = model.contains(clazz, RDF.type, OWL.Class);
	    System.out.println("(2) TEST QUERY: x-model://a/class is NOT in model model (before importing imprt). Is it? " + is_in_model);
	    System.out.println("--???? HOWEVER IT IS ??? -- BECAUSE OF DYNAMIC IMPORT SET??? BUT IT DOES NOT HAVE ANY EFFECT???");
	    
//	    assertTrue("import b->a does (??not) exist", model.hasLoadedImport(imp_ont_url));
	    
	    System.out.println("\n Has Import? " + model.hasLoadedImport(imp_ont_url));	    
	    
	    /*****
	     * 
	     * VPA Add subModel
	     * 
	     */
	    
	    model.addSubModel(imprt, true); ////VPA: OPTION? rebind - If true, rebind any associated inferencing engine to the new data (which may be an expensive operation)
	    
	    System.out.println("x-model://b (Main Model After SubModel loading)");
	    System.out.println("-------------------------------------------");
	    model.write(System.out, "N3");
	    System.out.println("\n");
	    
	    is_in_model = model.contains(clazz, RDF.type, OWL.Class);
	    System.out.println("(3) TEST QUERY: x-model://a/class is in model model (the model importing imprt). Is't it? " + is_in_model);
	    System.out.println("----");
	    
	    /*
	     * Write ALL also submodel
	     * 
	     */
	    
	    System.out.println("x-model://b (Main Model with the loaded SubModel)");
	    System.out.println("-------------------------------------------");
	    // EI TOIMI: model.writeAll(System.out, "N3"); //writeAll()
	    System.out.println("\n");

	    
	    //CLOSING MODEL
	    model.close();
	    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}

	public static void main(String[] args){
		
		MyBicycleWithImports myb = new MyBicycleWithImports();
		myb.test();
		
	}
	
}
