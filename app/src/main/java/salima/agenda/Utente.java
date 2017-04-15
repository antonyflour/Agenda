package salima.agenda;

import java.util.ArrayList;

public class Utente {
	
	private String nome;
	private String cognome;
	private String username;
	private String password;
	private ArrayList<Evento> eventi = new ArrayList<Evento>();
	
	public Utente(String username, String nome, String cognome, String password) {
		super();
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.password = password;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void addEvento(Evento e){
		this.eventi.add(e);
	}
	
	public ArrayList <Evento> getEventi(){
		return this.eventi;
	}
	
	
	public String toString(){
		return this.nome+" "+this.eventi;
			
	}
	


}
