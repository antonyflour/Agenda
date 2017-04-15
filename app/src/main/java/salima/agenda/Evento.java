package salima.agenda;


import java.io.PrintWriter; 
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Evento {
	
	
	private String id;
	private String descrizione;
	private String luogo;
	private String data;
	private String ora;
	private String tempoStimato;
	private String noteAggiuntive;
	private String categoria;
	
	public Evento(String descrizione, String luogo, String data, String ora, String tempoStimato,
			String noteAggiuntive, String categoria) {
		super();
		this.descrizione = descrizione;
		this.luogo = luogo;
		this.data = data;
		this.ora = ora;
		this.tempoStimato = tempoStimato;
		this.noteAggiuntive = noteAggiuntive;
		this.categoria= categoria;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getLuogo() {
		return luogo;
	}
	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getOra() {
		return ora;
	}
	public void setOra(String ora) {
		this.ora = ora;
	}
	public String getTempoStimato() {
		return tempoStimato;
	}
	public void setTempStimato(String tempStimato) {
		this.tempoStimato = tempStimato;
	}
	public String getNoteAggiuntive() {
		return noteAggiuntive;
	}
	public void setNoteAggiuntive(String noteAggiuntive) {
		this.noteAggiuntive = noteAggiuntive;
	}
	
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public String toString(){
		return this.id+" "+ " "+this.data;
			
	}

	@Override
	public boolean equals(Object o){
		Evento tmp = (Evento) o;
		if(this.getDescrizione().equalsIgnoreCase(tmp.getDescrizione()) &&
				this.getLuogo().equalsIgnoreCase(tmp.getLuogo()) &&
				this.getData().equalsIgnoreCase(tmp.getData())&&
				this.getOra().equalsIgnoreCase(tmp.getOra()) &&
				this.getTempoStimato().equalsIgnoreCase(tmp.getTempoStimato()) &&
				this.getNoteAggiuntive().equalsIgnoreCase(tmp.getNoteAggiuntive()) &&
				this.getCategoria().equalsIgnoreCase(tmp.getCategoria())
				)
			return true;
		return false;
	}



	
	
	
	
	
	
}
