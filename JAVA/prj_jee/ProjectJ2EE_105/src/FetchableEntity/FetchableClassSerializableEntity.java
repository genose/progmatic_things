/**
 * 
 */
package FetchableEntity;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * @author 59013-03-13
 *
 */
public class FetchableClassSerializableEntity implements EntityManager, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* ****************************************** */
	protected 	EntityManagerFactory 		entManagerFactory 	= null;
	protected 	EntityManager 				entManager 			= null;
	protected 	EntityTransaction 			entTransaction 		= null;
	private 	String 						entManagerString 	= null;
	/* ****************************************** */
	
	/**
	 * @return the entManagerFactory
	 */
	public EntityManagerFactory getEntManagerFactory() {
		return entManagerFactory;
	}

	/**
	 * @param entManagerFactory the entManagerFactory to set
	 */
	public void setEntManagerFactory(EntityManagerFactory entManagerFactory) {
		this.entManagerFactory = entManagerFactory;
		this.entManager = this.entManagerFactory.createEntityManager();
		this.entTransaction = this.entManager.getTransaction();
	}

	/**
	 * @return the entManager
	 */
	public EntityManager getEntManager() {
		return entManager;
	}

	/**
	 * @param entManager the entManager to set
	 */
	public void setEntManager(EntityManager entManager) {
		this.entManager = entManager;
		this.entManagerFactory = this.entManager.getEntityManagerFactory();
		this.entTransaction = this.entManager.getTransaction();
	}

	/**
	 * @return the entTransaction
	 */
	public EntityTransaction getEntTransaction() {
		return entTransaction;
	}

	/**
	 * @param entTransaction the entTransaction to set
	 */
	public void setEntTransaction(EntityTransaction entTransaction) {
		this.entTransaction = entTransaction;
	}

	public void entManagerFactoryInit(String argFactoryName) throws Exception {
		// TODO Auto-generated method stub
		entManagerString = argFactoryName;
		
		if(this.entManagerString == null ) {
			throw new Exception("entManager:: You should initialize entityManager with non-null Factory name (String) ... ");
		}
		
		/* ****************************************** */
		try {
			// if(this.entTransaction != null) this.entTransaction.rollback();
			// if(this.entManagerFactory != null) this.entManager.close();
			// if(this.entManagerFactory != null) this.entManagerFactory.close();
			
		}catch (Exception EV_ERR_PREPARE_ENT_MANAGER){
			System.out.println("Error : something went wrong when reset/initialize persistance manager : "+EV_ERR_PREPARE_ENT_MANAGER);
			throw EV_ERR_PREPARE_ENT_MANAGER;
		}
		/* ****************************************** */
		try {
			if(this.entManagerFactory == null)
				this.entManagerFactory = Persistence.createEntityManagerFactory(this.entManagerString);
			
		} catch (Exception EV_ERR_INIT_PERSISTMANAGER) {

			System.out.println("Error : can't initialize persistance manager : "+EV_ERR_INIT_PERSISTMANAGER);
			throw EV_ERR_INIT_PERSISTMANAGER;
		}
		/* ****************************************** */
		try {
			if(this.entManager == null)
				this.entManager = this.entManagerFactory.createEntityManager();
				
		} catch (Exception EV_ERR_INIT_PERSISTMANAGER) {

			System.out.println("Error : can't initialize persistance manager : entity : "+EV_ERR_INIT_PERSISTMANAGER);
			throw EV_ERR_INIT_PERSISTMANAGER;
		}
		/* ****************************************** */
		try {
			if(this.entTransaction == null)
				this.entTransaction = this.entManager.getTransaction();
		} catch (Exception EV_ERR_INIT_PERSISTMANAGER) {

			System.out.println("Error : can't initialize persistance manager : transaction : "+EV_ERR_INIT_PERSISTMANAGER);
			throw EV_ERR_INIT_PERSISTMANAGER;
		}
	} 
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> EntityGraph<T> createEntityGraph(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityGraph<?> createEntityGraph(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNamedQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createNamedStoredProcedureQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0, Class arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(CriteriaUpdate arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(CriteriaDelete arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String arg0, Class... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void detach(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	public <T> T find(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		if(this.entManagerString == null || this.entManagerFactory == null ) {
			throw new Exception("entManager:: You should initialize entityManager with this.[entManagerFactory]Init(String) ... ");
		}
		
		// http://www.java2s.com/Code/Java/Reflection/Usesreflectiontodisplaytheannotationassociatedwithamethod.htm
		// http://www.java2s.com/Code/Java/Reflection/FindAnnotatedFields.htm
		// http://www.java2s.com/Code/Java/Reflection/GetAnnotatedDeclaredFields.htm
		
		Class aClassID = null;
		Object aObjectIDClass = new Object();
		
	
		
		Class aObjectClass = arg0.getClass();
		Method[] aClassMethodDeclared = aObjectClass.getDeclaredMethods();
		Map<String,String> aClassMethodDeclaredMapping = new HashMap<String, String>();
		Field[] afield = aObjectClass.getDeclaredFields();
		
		
		for(Method aMethodDeclared : aClassMethodDeclared) {
			System.out.println(" Object class : adding method "+aMethodDeclared.getName());
			aClassMethodDeclaredMapping.put(aMethodDeclared.getName().toLowerCase(), aMethodDeclared.getName());
			
		}
		
		
		System.out.println(" Object class : "+String.valueOf( aObjectClass.toString())+" ::  fields Length : "+String.valueOf( afield.length));	
		
		List<Field> annotatedFields = new ArrayList<Field>(afield.length);
		
		for (Field field : afield) {
			Annotation[] aAnnontionsInfos = field.getAnnotations();
				
		  if( field.isAnnotationPresent(Id.class) ) {
		  // if (aAnnontionsInfos.length >0) {
			  
			  Object aObjectFieldValue = null; 
			  
			  String aFieldName = field.getName();
			  Method aMethodAccessor = null;
			  Class aReturnType = null;
			  try {
				  
				  String aMethodName = aClassMethodDeclaredMapping.get( "get"+aFieldName.toLowerCase() );
				  if(aMethodName == null) {
					  System.out.println("FetchableClassSerializableEntity.find() :: No method named ("+aMethodName+":["+aFieldName+"])");
					  continue;
				  }
				  aMethodAccessor = aObjectClass.getMethod(aMethodName);
				  if(aMethodAccessor != null) {
					  aObjectFieldValue = aMethodAccessor.invoke(arg0, null);
				  }else {
					  System.out.println("FetchableClassSerializableEntity.find() :: No method named ("+"get"+aFieldName+")");
					  continue;
				  }
				  aReturnType = aMethodAccessor.getReturnType();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("FetchableClassSerializableEntity.find() :: ERROR No method named ("+"get"+aFieldName+") :: "+e);
				
				continue;
			}
		
			  System.out.println(aFieldName+" :: "+((aObjectFieldValue != null)?aObjectFieldValue.getClass():"NULL CLASS")+" Annoted Length : "+String.valueOf( aAnnontionsInfos.length));
		    
			for (Annotation annoted : aAnnontionsInfos) {
				System.out.println("Annotation :: "+field.getName()+" :: "+(annoted).annotationType());
			}
			
			annotatedFields.add(field);
		    
		  }
	    }
		
		this.entManagerFactoryInit(this.entManagerString);
		
		System.out.println("EntManager should Find with :: "+arg0.getClass()+"::"+arg0.toString());
		Class<T> senderClassType = (Class<T>) arg0.getClass();
		return null;
	}
	/*
	public <T> T find(Class<T> arg0, Object<T> arg1, String argFactoryName) {
		// TODO Auto-generated method stub
		
		
		return null;
	}*/
	
	@Override
	public <T> T find(Class<T> arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2, Map<String, Object> arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityGraph<?> getEntityGraph(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlushModeType getFlushMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockModeType getLockMode(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metamodel getMetamodel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getReference(Class<T> arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityTransaction getTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isJoinedToTransaction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void joinTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lock(Object arg0, LockModeType arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T merge(T arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh(Object arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh(Object arg0, LockModeType arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFlushMode(FlushModeType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	// public find();
}
