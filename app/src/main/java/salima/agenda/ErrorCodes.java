package salima.agenda;

public class ErrorCodes {
	


		
	public static final int LOGIN_FAILED_CODE=300;
	public static final String LOGIN_FAILED = "Wrong username or password.";
	
	public static final int ALREADY_EXIST_USER_CODE=301;
	public static final String ALREADY_EXIST_USER = "User alredy exist";
	
	public static final int WRONG_USER_CODE=302;
	public static final String WRONG_USER = "Wrong user code.";
	
	
	public static final int UNKNOWN_RESOURCE_CODE=800;
	public static final String UNKNOWN_RESOURCE = "Unknown resource";
	
	public static final int ALREADY_EXIST_EVENT_CODE=303;
	public static final String ALREADY_EXIST_EVENT= "Already existing event .";
	
	public static final int NOT_EXIST_EVENT_CODE=304;
	public static final String NOT_EXIST_EVENT= "Not existing event .";
	    
	public static final int ALREADY_EXIST_CATEGORY_CODE=305;
	public static final String ALREADY_EXIST_CATEGORY= "Already existing category .";
	
	public static final int NOT_EXIST_CATEGORY_CODE=306;
	public static final String NOT_EXIST_CATEGORY= "Not existing category .";


	//MIEI
	public static final int UTENTE_INESISTENTE_EXC_NUMBER = 900;
	public static final int PASSWORD_ERRATA_EXC_NUMBER = 901;
	public static final int UTENTE_GIAESISTENTE_EXC_NUMBER = 903;
	public static final int EVENTI_INESISTENTI_EXC_NUMBER = 904;
	public static final int EVENTO_GIA_ESISTENTE_EXC_NUMBER = 905;
}
