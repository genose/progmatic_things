using System;
using System.Collections.Generic;
using System.Text;

namespace Hotel.Classes
{
    class Reservation
    {
        private Client client;
        private List<Chambre> chambres;
        private int numero;
        private static int compteur;
        private StatutReservation statut;

        public Client Client { get => client; set => client = value; }
        public List<Chambre> Chambres { get => chambres; set => chambres = value; }
        public int Numero { get => numero; set => numero = value; }
        public StatutReservation Statut { get => statut; set => statut = value; }

        public Reservation()
        {
            numero = ++compteur;
            Chambres = new List<Chambre>();
            Statut = StatutReservation.Validee;
        }

        public bool AjouterChambre(Chambre chambre)
        {
            //Ajouter la chambre à la liste des chambres de la réservation

            //Vérifie si la chambre existe déjà dans la liste des chambres de la réservation
            Chambre c = Chambres.Find(a => a.Numero == chambre.Numero);
            if(c == null)
            {
                Chambres.Add(chambre);
                //On change le statut de la chambre
                chambre.UpdateStatut(StatutChambre.Occupe);
                return true;
            }
            return false;
        }

        public bool AnnulerReservation()
        {
            //Changer le statut et liberer les chambres
            Statut = StatutReservation.Annulee;
            Chambres.ForEach(c =>
            {
                c.UpdateStatut(StatutChambre.Libre);
            });
            return false;
        }

        public override string ToString()
        {
            string retour = $"Numero {Numero}, Statut {Statut}\n";
            retour += "Liste chambres\n";
            //Chambres.ForEach(c =>
            //{
            //    retour += c.ToString() + "\n";
            //});
            foreach(Chambre c in Chambres)
            {
                retour += c.ToString() + "\n";
            }
            return retour;
        }
    }

    enum StatutReservation
    {
        Validee,
        Annulee
    }
}
