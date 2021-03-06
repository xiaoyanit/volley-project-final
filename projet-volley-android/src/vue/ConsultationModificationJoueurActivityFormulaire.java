package vue;
import java.util.ArrayList;
import java.util.List;

import modele.Equipe;
import modele.InitialisationModele;
import modele.Joueur;
import modele.JoueurEquipe;

import com.l3info.projet_volley_android.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import controleur.*;

public class ConsultationModificationJoueurActivityFormulaire extends Activity{
	
	private Controleur ctl = Controleur.getInstance(); //acces � la BD
	
	private List<Equipe> equipes=new ArrayList<Equipe>(); // permet de sauvegarder les ?quipes de la BD 
	
	private OnClickListener clikSurBouton = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//Construction des pop-ups (erreur et confirmation)
			AlertDialog.Builder messErreur = new AlertDialog.Builder(ConsultationModificationJoueurActivityFormulaire.this);
			messErreur.setTitle("Erreur");
			messErreur.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
			
			AlertDialog.Builder messConfirmation = new AlertDialog.Builder(ConsultationModificationJoueurActivityFormulaire.this);
			messConfirmation.setTitle("");
			messConfirmation.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					Intent intent = new Intent(ConsultationModificationJoueurActivityFormulaire.this, ConsultationModificationSuppressionJoueurActivity.class);
					intent.putExtra("mode","modification");	
					startActivity(intent);
					finish();
					ConsultationModificationJoueurActivityFormulaire.this.finish();
					dialog.cancel();
				}
			});
			
			if (v.getId()==R.id.ValidationModifieJoueur)// bouton modifier
			{
				if ( (tailleEnModification.getText().toString().length()>0) && (ageEnModification.getText().toString().length()>0) && (posteEnModification.getText().toString().length()>0) ) //traitement pour les champs numeriques
				{
					Intent monIntent = getIntent();
					
					Joueur monJoueur = new Joueur(monIntent.getIntExtra("id",0),nomEnModification.getText().toString(),Integer.parseInt(tailleEnModification.getText().toString()),Integer.parseInt(ageEnModification.getText().toString()),Integer.parseInt(posteEnModification.getText().toString()));
					JoueurEquipe monJoueurEquipe;
				
					if (numMaillotEnModification.getText().toString().length()>0)
					{
						monJoueurEquipe = new JoueurEquipe(monIntent.getIntExtra("idJoueurEquipe",0),monJoueur,equipes.get(listeEquipe.getCheckedItemPosition()),Integer.parseInt(numMaillotEnModification.getText().toString()),true);

					}
					else
					{
						monJoueurEquipe = new JoueurEquipe(monIntent.getIntExtra("idJoueurEquipe",0),monJoueur,equipes.get(listeEquipe.getCheckedItemPosition()),-1,true);

					}
					
					if (monJoueur.nomEstValide())
					{
						if(monJoueur.ageEstValide())
						{
							if(monJoueur.tailleEstValide())
							{
								if(monJoueur.posteEstValide())
								{
									if (equipes.get(listeEquipe.getCheckedItemPosition()).getId()!=-1)
									{
										if(monJoueurEquipe.numMaillotEstValide())
										{
											ctl.jb.open();
											ctl.jb.modifier(monJoueur);
											ctl.jb.close();
											
											ctl.jeb.open();
											ctl.jeb.modifier(monJoueurEquipe);
											ctl.jeb.close();
											
											messConfirmation.setTitle("");
											messConfirmation.setMessage("Joueur modifi�");
											AlertDialog alertErreur = messConfirmation.create();
											alertErreur.show();
											
										}
										else
										{
											messErreur.setMessage("n� maillot est invalide");
											AlertDialog alertErreur = messErreur.create();
											alertErreur.show();
											numMaillotEnModification.setText("");
										}
									}
									else
									{
										ctl.jb.open();
										ctl.jb.modifier(monJoueur);
										ctl.jb.close();
										
										
										ctl.jeb.open();
										ctl.jeb.modifier(monJoueurEquipe);
										ctl.jeb.close();
	
										messConfirmation.setTitle("");
										messConfirmation.setMessage("Joueur modifi�");
										AlertDialog alertErreur = messConfirmation.create();
										alertErreur.show();
									}
									
								}
								else
								{
									messErreur.setMessage("poste est invalide");
									AlertDialog alertErreur = messErreur.create();
									alertErreur.show();
									posteEnModification.setText("");
								}
							}
							else
							{
								messErreur.setMessage("taille est invalide");
								AlertDialog alertErreur = messErreur.create();
								alertErreur.show();
								tailleEnModification.setText("");
							}
						}
						else
						{
							messErreur.setMessage("age est invalide");
							AlertDialog alertErreur = messErreur.create();
							alertErreur.show();
							ageEnModification.setText("");
						}
					}
					else
					{
						messErreur.setMessage("nom est invalide");
						AlertDialog alertErreur = messErreur.create();
						alertErreur.show();
						nomEnModification.setText("");
					}
				}
				else
				{
					messErreur.setMessage("Veuillez remplir les champs vides");
					AlertDialog alertErreur = messErreur.create();
					alertErreur.show();
				}
			}
			else if (v.getId()==R.id.Precedent)// bouton precedent
			{
				Intent intent = new Intent(ConsultationModificationJoueurActivityFormulaire.this, ConsultationModificationSuppressionJoueurActivity.class);
				
				if (monIntent.getStringExtra("mode").equals("consultation"))
				{
					intent.putExtra("mode","consultation");	
				}
				else if (monIntent.getStringExtra("mode").equals("modification"))
				{
					intent.putExtra("mode","modification");
				}
				
				startActivity(intent);
				finish();
				
			}
			else 
			{
				Intent intent = new Intent(ConsultationModificationJoueurActivityFormulaire.this, MainActivity.class);
				startActivity(intent);
			}
			
			
		}
	};
	
	private OnItemClickListener clickSurItemListe = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
			
			if (parent.getId()==R.id.listeEquipes) // clique sur un item de la liste des ?quipes			
			{
				
				if (equipes.get(listeEquipe.getCheckedItemPosition()).getId()!=monIntent.getIntExtra("idEquipe",0))
				{
					numMaillotEnModification.setText("");
				}
				else
				{
					if (equipes.get(listeEquipe.getCheckedItemPosition()).getId()!=-1)
					{
						numMaillotEnModification.setText(monIntent.getIntExtra("numMaillot",0));
					}
					else
					{
						numMaillotEnModification.setText("");
					}
					
				}
				
				// gestion de la visibilit� du champ
				if (equipes.get(listeEquipe.getCheckedItemPosition()).getId()!=-1)
				{
					numMaillotEnModification.setVisibility(View.VISIBLE);
				}
				else
				{
					numMaillotEnModification.setVisibility(View.INVISIBLE);
				}
			}
			
		}
		
	};
	
	TextView titre=null;
	Button precedent = null;
	Button accueil = null;
	
	// element de la page de consultation
	TextView nomEnConsultation=null;
	TextView ageEnConsultation=null;
	TextView tailleEnConsultation=null;
	TextView posteEnConsultation=null;
	TextView numMaillotEnConsultation=null;
	TextView equipeEnConsultation=null;
	
	// element de la page de modification
	Button modifier = null;
	ListView listeEquipe = null;
	EditText nomEnModification=null;
	EditText ageEnModification=null;
	EditText tailleEnModification=null;
	EditText posteEnModification=null;
	EditText numMaillotEnModification=null;
	
	Intent monIntent;
	
	public void onCreate(Bundle savedInstanceState)
	{
		List<Equipe> equipesBD;
		
		ctl.initialiseBdd(ConsultationModificationJoueurActivityFormulaire.this);
		
		ctl.eb.open();
		
		equipesBD = ctl.eb.selectionnerTout();
		
		ctl.eb.close();
		
		super.onCreate(savedInstanceState);
		
		
		monIntent = getIntent();
		
		if (monIntent.getStringExtra("mode").equals("consultation"))//chargement des elements de la fenetre en fonction du mode
		{
			setContentView(R.layout.consulter_joueur_1_activity);
			
			nomEnConsultation=(TextView) findViewById(R.id.libNomJoueur);
			ageEnConsultation=(TextView) findViewById(R.id.libAgeJoueur);
			tailleEnConsultation=(TextView) findViewById(R.id.libTailleJoueur);
			posteEnConsultation=(TextView) findViewById(R.id.libPosteJoueur);
			numMaillotEnConsultation=(TextView) findViewById(R.id.libNumMaillot);
			equipeEnConsultation=(TextView) findViewById(R.id.libEquipeJoueur);
			titre=(TextView) findViewById(R.id.libConsulterJoueur1);
			
			titre.setText("Consultation d'un joueur");
			nomEnConsultation.setText(nomEnConsultation.getText()+"   "+monIntent.getStringExtra("nom"));
			ageEnConsultation.setText(ageEnConsultation.getText()+"   "+monIntent.getIntExtra("age",0)+ " ans");
			tailleEnConsultation.setText(tailleEnConsultation.getText()+"   "+monIntent.getIntExtra("taille",0)+ " cm");
			posteEnConsultation.setText(posteEnConsultation.getText()+"   "+monIntent.getIntExtra("poste",0));
			numMaillotEnConsultation.setText(numMaillotEnConsultation.getText()+"   "+monIntent.getIntExtra("numMaillot",0));
			equipeEnConsultation.setText(equipeEnConsultation.getText()+"   "+monIntent.getStringExtra("nomEquipe"));
			
			if (monIntent.getIntExtra("idEquipe",0)==-1)
			{
				numMaillotEnConsultation.setVisibility(View.INVISIBLE);
			}
			
		}
		else
		{
			setContentView(R.layout.modification_joueur_etape2_activity);
			
			modifier=(Button) findViewById(R.id.ValidationModifieJoueur);
			listeEquipe=(ListView) findViewById(R.id.listeEquipes);
			titre=(TextView) findViewById(R.id.libModificationJoueur);
			nomEnModification=(EditText) findViewById(R.id.nomJoueur);
			ageEnModification=(EditText) findViewById(R.id.AgeJoueur);
			tailleEnModification=(EditText) findViewById(R.id.tailleJoueur);
			posteEnModification=(EditText) findViewById(R.id.Poste_en_courJoueur);
			numMaillotEnModification=(EditText) findViewById(R.id.numMaillot);
			

			titre.setText("Modification d'un joueur");
			nomEnModification.setText(monIntent.getStringExtra("nom"));
			ageEnModification.setText(""+monIntent.getIntExtra("age",0));
			tailleEnModification.setText(""+monIntent.getIntExtra("taille",0));
			posteEnModification.setText(""+monIntent.getIntExtra("poste",-1));
			numMaillotEnModification.setText(""+monIntent.getIntExtra("numMaillot",0));
			
			
			if (monIntent.getIntExtra("idEquipe",0)==-1)
			{
				numMaillotEnModification.setVisibility(View.INVISIBLE);
			}
			
			// initialisation du contenu de chaque item pr?sent dans la liste
			ArrayList<String> listeNomEquipe = new ArrayList<String>();

			
			// premier element qui sera coch� � l'initialisation et qui correspond au club du joueur
			equipes.add(new Equipe(monIntent.getIntExtra("idEquipe",0),monIntent.getStringExtra("nomEquipe"),""));
			listeNomEquipe.add(monIntent.getStringExtra("nomEquipe"));
			
			for (int i=0; i<equipesBD.size();i++)
			{
				if (equipesBD.get(i).getId()!=monIntent.getIntExtra("idEquipe",0))
				{
					equipes.add(equipesBD.get(i));
					listeNomEquipe.add(equipesBD.get(i).getNom());
				}
			}
			
			// si il possede un club, il faut ajouter "sans club" � la listView
			if (monIntent.getIntExtra("idEquipe",0)!=-1)
			{
				listeNomEquipe.add("Sans club");
				equipes.add(new Equipe(-1,"Sans club",""));
			}
			
			
			// creation et initialisation de la liste des ?quipes 
			listeEquipe.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,listeNomEquipe));
			
			// selection de l'?quipe en fonction de IndClubJoueur
			
			
			listeEquipe.setItemChecked(0,true);
			
			
			modifier.setOnClickListener(clikSurBouton);
			listeEquipe.setOnItemClickListener(clickSurItemListe);
		}
		
		precedent = (Button) findViewById(R.id.Precedent);
		accueil = (Button) findViewById(R.id.RetourAccueil);
		
		//gestion des actions issue du clique en fonction des boutons 
		precedent.setOnClickListener(clikSurBouton);
		accueil.setOnClickListener(clikSurBouton);
		
	}


}


