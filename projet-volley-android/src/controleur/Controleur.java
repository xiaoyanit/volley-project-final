package controleur;
import java.util.ArrayList;
import java.util.List;

import vue.ChoixEquipesMatch;
import vue.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import basededonnees.*;
import modele.*;

/**
 * @author Vincent
 *
 */
public class Controleur {
	
	private static Controleur instance;
	private Modele modele;
	public JoueurBdd jb;
	public JoueurActionBdd jab;
	public ActionBdd ab;
	public EquipeBdd eb;
	public JoueurEquipeBdd jeb;
	public MatchBdd mb;
	public CompetitionBdd cb;
	public SetBdd sb;
	
	public static void main(String[] args){
		Controleur controleur = Controleur.getInstance();
	}
	
	protected Controleur(){
		modele = new Modele();
	}
	
	public static synchronized Controleur getInstance(){
		if(instance == null){
			instance = new Controleur();
		}
		return instance;
	}
	
	public void initialiseBdd(Context c){
		this.jb = new JoueurBdd(c);
		this.jab = new JoueurActionBdd(c);
		this.ab = new ActionBdd(c);
		this.eb = new EquipeBdd(c);
		this.jeb = new JoueurEquipeBdd(c);
		this.mb = new MatchBdd(c);
		this.cb = new CompetitionBdd(c);
		this.sb = new SetBdd(c);
	}
	
	public void testBdd(){
		this.eb.open();
		List<Equipe> e = this.eb.selectionnerTout();
		this.eb.close();
		if(e.size() > 0){
			return;
		}
		this.eb.open();
		Equipe e1 = new Equipe(0, "VolleyRoxor", "Jacques Palmier");
		Equipe e2 = new Equipe(0, "SmasheurFou", "Fredon Sacqu�");
		
		this.eb.open();
		e1.setId((int)this.eb.ajouter(e1));
		e2.setId((int)this.eb.ajouter(e2));
		this.eb.close();
		
		for(int i = 1; i <= 24; i++){
			Equipe e3 = e1;
			if(i >= 13){
				e3 = e2;
			}
			this.jb.open();
			Joueur j = new Joueur(0, "Joueur"+i, 170+i, 20+i, 1);
			j.setId((int)this.jb.ajouter(j));
			this.jb.close();
			this.jeb.open();
			JoueurEquipe je = new JoueurEquipe(0, j, e3, i%6, true);
			this.jeb.ajouter(je);
			this.jeb.close();
		}
		
	}
	
	// Methodes acc�s Mod�le
	public Modele getModele(){return modele;}
	public boolean estNouveauMatch(){return modele.estNouveauMatch();}
	public boolean estNouveauSet(){return modele.estNouveauSet();}
	public boolean estNouveauPoint(){return modele.estNouveauPoint();}

	public String getEtatAuto(){return modele.getEtatAuto();}
	public int getService(){return modele.getService();}
	public ArrayList<String> getActionsPossibles(){return modele.getEtatsAuto();}
	public int getJSuiv(){return modele.getJSuiv();}
	public void echangeJ(int eq, int j1, int j2)
	{
		j1 = j1%12;
		j2 = j2%12;
		if (eq == 0)
		{
			
			modele.echangeBleu(j1, j2);
			System.out.println(modele.getJoueur(j2).getNom() + "  ====>  " + modele.getJoueur(j1).getNom());
			
		}
		else if (eq ==1)
		{
			modele.echangeRouge(j1, j2);
			System.out.println(modele.getJoueur(j2).getNom() + "  ====>  " + modele.getJoueur(j1).getNom());
		}else System.out.println("EQUIPE INVALIDE ECHANGEJ()");
		
	}
	// A MODIFIER POUR ID ET POSTE JOUEUR
	public boolean soumettreAction(int j1, int j2, String type, int note)
	{
		System.out.println("J1 est " + j1);
		System.out.println("J2 est " + j2);
		int eq = 0;
		if (j1 < 12){eq = 1;}
		
		Action action = new Action(0, type);
		System.out.println("Joueur " + modele.getJoueur(j1).getNom() + " effectue un " + type + " " + note + " sur le joueur " + modele.getJoueur(j2).getNom());
		ActionJoueur act = new ActionJoueur(0, modele.getSet(), action, modele.getJoueur(j1), modele.getNumPoint(), note, 0);
		modele.ajouterAction(act);
		modele.setJSuiv(j2);
		
		if ((note == 2) && ((type == "at") || (type == "bl") || (type == "se")))
		{
			modele.setGagne(eq);
		}else if (note == -2){modele.setGagne((eq+1)%2);}
		
		if (modele.getGagne() == -1)
		{
			modele.avancerAuto(type);
			modele.setNouveauPoint(false);
		}
		else 
		{
			System.out.println("Equipe " + modele.getGagne() + " remporte le point");
			System.out.println("Test si changement de service : " + modele.getService() + " VS " + modele.getGagne());
			if (modele.getService() != modele.getGagne())
			{
				modele.setService(modele.getGagne());
				modele.setRotation(modele.getGagne());
				
			}
			modele.resetAuto();
			modele.setNouveauPoint(true);
			soumettrePoint();
			modele.incrementePoint(modele.getGagne());
			modele.setGagne(-1);
			modele.afficherScore();
			
			Set set = modele.getSet();
			if (modele.getTabSet().size() < 5)
			{
				if ((set.getScoreEquipeDomicile() >= 25 || set.getScoreEquipeExterieur() >= 25) && (Math.abs(set.getScoreEquipeDomicile()-set.getScoreEquipeExterieur()) >= 2))
				{
					System.out.println("Gain de set d�tect� !");
					if (finMatch() != -1)
					{
						modele.setFinMatch(true);
					}
					else{modele.setNouveauSet(true);}
					
				}
			}
			else
			{
				if ((set.getScoreEquipeDomicile() >= 15 || set.getScoreEquipeExterieur() >= 15) && (Math.abs(set.getScoreEquipeDomicile()-set.getScoreEquipeExterieur()) >= 2))
				{
					modele.setFinMatch(true);
				} 
			}
			
			
		}
		
		
		
		
		
		return true;
	}
	
	public int finMatch()
	{
		int cptEq1 = 0;
		int cptEq2 = 0;
		for (Set s : modele.getTabSet())
		{
			if (s.getScoreEquipeDomicile() > s.getScoreEquipeExterieur())
			{
				cptEq1 ++;
			}
			else {cptEq2 ++;}
		}
		if (cptEq1 == 3){return 0;}
		else if (cptEq2 == 3){return 1;}
		else {return -1;}
		
	}
	
	public void nouveauSet()
	{
		modele.ajouterNouveauSet();
		modele.setNouveauSet(false);
	}
	
	public void nouveauPoint()
	{
		modele.nouveauPoint();
	}
	
	public void soumettrePoint(){
		modele.getPoint().sauvegarderPoint();
	}
	
	
}

	