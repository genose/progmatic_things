using System;
using System.Collections.Generic;
using System.Text;

namespace Hotel.Classes
{
    public class Chambre 
    {
        private int numero;
      
        private decimal tarif;
        private int nbOccp;
        private StatutChambre statut;
        public event Action<int, StatutChambre> ChangeStatut;

        public int Numero { get => numero; set => numero = value; }
        public decimal Tarif { get => tarif; set => tarif = value; }
        public int NbOccp { get => nbOccp; set => nbOccp = value; }
        public StatutChambre Statut { get => statut; set { 
                statut = value;
                if (ChangeStatut != null)
                {
                    ChangeStatut(Numero, value);
                }
            } 
        }

        private static int compteur = 0;
        public Chambre()
        {
            Numero = ++compteur;
        }
        public void UpdateStatut(StatutChambre statut)
        {
            //Mettre à jour le statut de la chambre et invoquer l'event ChangeStatut
            Statut = statut;
            if (ChangeStatut != null)
            {
                ChangeStatut(Numero, Statut);
            }
            //ChangeStatut?.Invoke(Numero, Statut);
        }

        public override string ToString()
        {
            return $"Numero {Numero}, Tarif : {Tarif}, Statut : {Statut}";
        }
    }

    public enum StatutChambre
    {
        Libre,
        Occupe
    }
}
