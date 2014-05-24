package basededonnees;

import java.util.ArrayList;
import java.util.List;

import controleur.Controleur;
import modele.Match;
import modele.Set;
import android.content.Context;
import android.database.Cursor;
import android.content.*;


/**
 * @author Thibaut C
 *
 */
public class SetBdd extends BDD {
	  public SetBdd(Context pContext){
		  super(pContext);
	  }
	  /**
	   * @param s le set � ajouter � la base
	   */
	  public long ajouter(Set s) {
		  ContentValues value = new ContentValues();
		  value.put("numS", s.getNumSet());
		  value.put("scoreEquipe1S", s.getScoreEquipeDomicile());
		  value.put("scoreEquipe2S", s.getScoreEquipeExterieur());
		  value.put("matchS", s.getMatch().getId());
		  return mDb.insert("SETS", null, value);
	  }

	  /**
	   * @param id l'identifiant du set � supprimer
	   */
	  public void supprimer(int id) {
		  mDb.delete("SETS", "idS = ?", new String[]{String.valueOf(id)});
	  }

	  /**
	   * @param s le set modifi�
	   */
	  public void modifier(Set s) {
		  ContentValues value = new ContentValues();
		  value.put("numS", s.getNumSet());
		  value.put("scoreEquipe1S", s.getScoreEquipeDomicile());
		  value.put("scoreEquipe2S", s.getScoreEquipeExterieur());
		  value.put("matchS", s.getMatch().getId());
		  mDb.update("SETS", value, "idS = ?", new String[] {String.valueOf(s.getId())});
	  }

	  /**
	   * @param i l'identifiant du set � r�cup�rer
	   */
	  /*
	  public Set selectionner(int i){
		  Cursor c = mDb.rawQuery("SELECT * FROM SETS WHERE idS = ?", new String[]{String.valueOf(i)});
		  c.moveToFirst();
		  return new Set(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), leMatch);
	  }*/
	  
	  public List<Set> selectionnerTout(){
		Cursor c = mDb.rawQuery("SELECT * FROM SETS", null);
		List<Set> sets = new ArrayList<Set>();
		Controleur ca = Controleur.getInstance();
		while(c.moveToNext()){
			sets.add(new Set(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), ca.mb.selectionner(c.getInt(4))));
		}
		c.close();
		return sets;
	  }
	}