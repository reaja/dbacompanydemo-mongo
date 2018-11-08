package net.gumbix.dba.companydemo.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
//import com.google.common.collect.Lists;
import net.gumbix.dba.companydemo.db.AbstractDBAccess;
import net.gumbix.dba.companydemo.domain.Address;
import net.gumbix.dba.companydemo.domain.Car;
import net.gumbix.dba.companydemo.domain.CompanyCar;
import net.gumbix.dba.companydemo.domain.Department;
import net.gumbix.dba.companydemo.domain.Employee;
import net.gumbix.dba.companydemo.domain.Personnel;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.StatusReport;
import net.gumbix.dba.companydemo.domain.WorksOn;

public class MongoDbAccess extends AbstractDBAccess {

	public MongoDatabase db;
	public MongoClient mClient;
	public BasicDBObject doc;
	public BasicDBObject addresseVonPersonel;
	public MongoCollection<Document> collection;

	public MongoDbAccess(String string) {
		startClient();
	}

	@SuppressWarnings("deprecation")
	private void startClient() {
		try {

			mClient = new MongoClient("localhost", 27017);
			db = mClient.getDatabase("firmenwelt");
			db.createCollection("Personal");
			db.createCollection("Department");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public Personnel loadPersonnel(long persNr) throws Exception {
		collection = db.getCollection("Personal");
		Personnel temp = null;
		List<Document> documents = (List<Document>) collection.find(Filters.eq("PersonalID", persNr)).into(new ArrayList<Document>());
		for(int i = 0; i < documents.size(); i++) {
			temp = new Personnel(persNr, (String)documents.get(i).get("firstName"), (String)documents.get(i).get("firstName"), 
					new Date(), new Address((String)documents.get(i).get("Srtra�e"),
							(String)documents.get(i).get("Hausnummer"), (String)documents.get(i).get("PLZ"),(String)documents.get(i).get("City")));
		}
		return temp;
	}

	@Override
	public List<Personnel> queryPersonnelByName(String firstName, String lastName) throws Exception {
//		collection = db.getCollection("Personal");
//		List<Personnel> temp = new List();
//		List<Document> documents = (List<Document>) collection.find(Filters.eq("firstName", firstName)).into(new ArrayList<Document>());
//		for(int i = 0; i < documents.size(); i++) {
//			temp = new List<Personnel>();
//			
//		}
//		return temp;
		return null;
	}

	@Override
	public void storePersonnel(Personnel pers) throws Exception {
		// Artibute von Personell hinzuf�gen
		MongoCollection<Document> collection = db.getCollection("Personal");
		Document document = new Document("PersonalID", pers.getPersonnelNumber()).append("lastName", pers.getLastName())
				.append("firsName", pers.getFirstName()).append("Birthdate", pers.getBirthDate().toString())
				.append("salary", pers.getSalary()).append("Srtra�e", pers.getAddress().getStreet())
				.append("Hausnummer", pers.getAddress().getHouseNumber()).append("PLZ", pers.getAddress().getZip())
				.append("City", pers.getAddress().getZipCity().getCity());
		collection.insertOne(document);
	}

	@Override
	public void deletePersonnel(Personnel pers) throws Exception {
		collection = db.getCollection("Personal");
		collection.deleteOne(Filters.eq("PersonalID", pers.getPersonnelNumber()));
	}

	@Override
	public Department loadDepartment(long depNumber) throws Exception {
		collection = db.getCollection("Department");
		Department temp = null;
		List<Document> documents = (List<Document>) collection.find(Filters.eq("DepNr", depNumber)).into(new ArrayList<Document>());
		for(int i = 0; i < documents.size(); i++) {
			temp = new Department(depNumber, (String) documents.get(i).get("name"));
		}
		return temp;
	}

	@Override
	public List<Department> queryDepartmentByName(String queryString) throws Exception {
//		collection = db.getCollection("Department");
//		List<Department> temp = null;
//		List<Document> documents = (List<Document>) collection.find(Filters.eq("DepNr", depNumber)).into(new ArrayList<Document>());
//		for(int i = 0; i < documents.size(); i++) {
//			temp = new List<Department>();
//		}
//		return temp;
		return null;
	}

	@Override
	public void storeDepartment(Department department) throws Exception {
		MongoCollection<Document> collection = db.getCollection("Department");
		Document document = new Document("DepNr", department.getDepNumber()).append("Name", department.getName());
		collection.insertOne(document);
	}

	@Override
	public void deleteDepartment(Department department) throws Exception {
		collection = db.getCollection("Department");
		collection.deleteOne(Filters.eq("DepNr", department.getDepNumber()));
	}

	@Override
	public Car loadCar(String modell) throws Exception {
		collection = db.getCollection("Car");
		Car temp = null;
		List<Document> documents = (List<Document>) collection.find(Filters.eq("Modell", modell)).into(new ArrayList<Document>());
		for(int i = 0; i < documents.size(); i++) {
			temp = new Car(modell,(String) documents.get(i).get("name"));
		}
		return temp;
	}

	@Override
	public void storeCar(Car car) throws Exception {
		MongoCollection<Document> doc = db.getCollection("Car");
		Document document = new Document("Modell", car.getModel()).append("Type", car.getType());
		doc.insertOne(document);
	}

	@Override
	public void deleteCar(Car car) throws Exception {
		collection = db.getCollection("Car");
		collection.deleteOne(Filters.eq("Modell", car.getModel()));
	}

	@Override
	public CompanyCar loadCompanyCar(String licensePlate) throws Exception {
		collection = db.getCollection("Firmenwagen");
		CompanyCar temp = null;
		List<Document> documents = (List<Document>) collection.find(Filters.eq("Kennzeichen", licensePlate)).into(new ArrayList<Document>());
		for(int i = 0; i < documents.size(); i++) {
			temp = new CompanyCar(licensePlate, new Car(documents.get(i).getString("Modell"),documents.get(i).getString("Type")));
		}
		return temp;
	}

	@Override
	public List<CompanyCar> queryCompanyCarByModel(String model) throws Exception {
		collection = db.getCollection("Firmenwagen");
		List<CompanyCar> temp = new ArrayList();
		List<Document> documents = (List<Document>) collection.find(Filters.eq("Modell", model)).into(new ArrayList<Document>());
		for(int i = 0; i < documents.size(); i++) {
			temp.add(new CompanyCar(documents.get(i).getString("Kennzeichen"),
					new Car(documents.get(i).getString("Modell"), documents.get(i).getString("Type"))));
		}
		return temp;
	}

	@Override
	public void storeCompanyCar(CompanyCar car) throws Exception {
		MongoCollection<Document> collection = db.getCollection("CompanyCar");
		// Logikfehler in der UI! temp wird erzeugt und umgeht den fehler 
		Car temp = new Car("Polo", "VW");
		Document document = new Document("Kennzeichen", car.getLicensePlate()).append("Modell", temp.getModel())
				.append("Marke", temp.getType()).append("Fahrer", car.getDriver());
		collection.insertOne(document);
	}

	@Override
	public void deleteCompanyCar(CompanyCar car) throws Exception {
		collection = db.getCollection("CompanyCar");
		collection.deleteOne(Filters.eq("Kennzeichen", car.getLicensePlate()));
	}

	@Override
	public Project loadProject(String projId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Project> queryProjectByDescription(String queryString) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeProject(Project proj) throws Exception {
		collection = db.getCollection("Project");
		doc = new BasicDBObject();
		doc.put("beschreibung", proj.getDescription());
		// doc.put("", arg1)
	}

	@Override
	public void deleteProject(Project proj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public StatusReport loadStatusReport(Project project, long continuousNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StatusReport> loadStatusReport(Project project) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeStatusReport(StatusReport rep) throws Exception {
		collection = db.getCollection("statusReport");
		doc = new BasicDBObject();
		doc.put("content", rep.getContent());

		doc.put("continuousNumber", rep.getContinuousNumber());
		doc.put("date", rep.getDate());
		doc.put("prjact", rep.getProject());

		// dbColletction.insert(doc);

	}

	@Override
	public void deleteStatusReport(StatusReport rep) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<WorksOn> loadWorksOn(Employee employee) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<WorksOn> loadWorksOn(Project proj) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeWorksOn(WorksOn wo) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteWorksOn(WorksOn wo) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfPersonnel() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfProjects() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Employee> getIdleEmployees() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
