/* OntologyImport.java
 * 2016-09-28 TOIMII!!!
 * Copied from "\javalab\eclwork\2014_Luna\SpinExamplesLib133AsSrc\src\siima   utils\tests
 * dump_methods.txt
 * Referencing:
 * From: http://stackoverflow.com/questions/17292675/read-only-file-instances-of-an-ontology-model-in-jena
 * 
 */


package siima.tests;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologyImport {

	public OntModel loadModelWithSubModels(List<String> urls, List<String> altlocs) {
		/* VPA: 2016-02-15 See MyBicycleWithImports.java test-case 
		 * TODO: EI TOIMI -> joten kokeilin test() methodilla -> edelleen ongelmia resoner mallia luotaessa.
		 * VPA: SubModels need to be loaded first??
		 * ELSE: An error occurred while attempting to read from http://siima.net/ont/accessories. 
		 * Msg was 'java.net.UnknownHostException: siima.net'.
		 * 
		 * */
			InputStream is;
			// Create a doc manager / modelspec to resolve imports
		    final OntDocumentManager docManager = new OntDocumentManager();
		    final OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
		    
		    modelSpec.setDocumentManager(docManager);

		    OntModel imprt=null;
		    	
			for (int i = 1; i < urls.size(); i++) { //Note: start from index=1
				//(urls.get(i), altlocs.get(i));
				 // Create an imported model that can be referenced by the docManager
			    imprt = ModelFactory.createOntologyModel(modelSpec);
			    docManager.addModel(urls.get(i), imprt);
			    try {
					is = new BufferedInputStream(new FileInputStream(altlocs.get(i))); //altlocs.get(i)
				
				imprt.read(is,urls.get(i), "TURTLE");
			    } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    System.out.println("(Load SubModel n:o (" + i + "))");
			    System.out.println("-------------------------------------------");
			    
			}
			
			 // Create a basemodel 
		    final OntModel baseModel = ModelFactory.createOntologyModel(modelSpec);
		    baseModel.setDynamicImports(true); //VPA: OPTION?
		try {  		    
		    is = new BufferedInputStream(new FileInputStream(altlocs.get(0)));		
		    baseModel.read(is,urls.get(0), "TURTLE");
		    
		    System.out.println("(Base Model Before imports)");
		    System.out.println("-------------------------------------------");
		    
		    baseModel.addSubModel(imprt, true);
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return baseModel;
		}
		
	
	public static void main(String[] args) {
		
		List<String> altlocs = new ArrayList<String>();
		List<String> urls = new ArrayList<String>();
		
		/* --- Main ontology --- */
		String ont_folder = "data/models/importing_models"; 
		
		String main_ont_file= "bicycle.ttl"; 
		String main_ont_url="http://siima.net/ont/bicycle"; 
		
		altlocs.add(ont_folder + "/" + main_ont_file);
		urls.add(main_ont_url);
		
		/* --- Imported ontologies --- */		
		String imp_ont_file="accessories.ttl"; 
		String imp_ont_url= "http://siima.net/ont/accessories"; 
		
		altlocs.add(ont_folder + "/" + imp_ont_file);
		urls.add(imp_ont_url);
		//--------------------
		System.out.println("======= LOADING ONTOLOGY WITH IMPORTS  ======");
		OntologyImport oimp = new OntologyImport();
		
		OntModel resultmodel = oimp.loadModelWithSubModels(urls, altlocs);
		
		System.out.println("--- result ontology checking ---");
		Boolean has = resultmodel.hasLoadedImport("http://siima.net/ont/accessories");
		System.out.println("imported model exist?: " + resultmodel.hasLoadedImport("http://siima.net/ont/accessories"));
		//---BASEMODEL (Find (ctrl+f) "owl:imports" from the console print!)
		resultmodel.getBaseModel().write(System.out, "TURTLE");
		//---IMPORTED MODEL
		//OntModel imported = resultmodel.getImportedModel(imp_ont_url);
		//imported.write(System.out, "TURTLE");
		//---COMBINED MODEL ? OR just the basemodel
		//resultmodel.write(System.out, "TURTLE");
		
		// From: the StackOverFlow link
		// Ontology logilogi = imported.createOntology("http://siima.net/ont/accessories"); //
		
		

	}

}
