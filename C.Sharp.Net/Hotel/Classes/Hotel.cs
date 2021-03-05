using Hotel.Tools;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Hotel.Classes
{
    class Hotel
    {
        private string nom;
        private List<Client> clients;
        private List<Chambre> chambres;
        private List<Reservation> reservations;
        
        public string Nom { get => nom; set => nom = value; }
        public List<Client> Clients { get => clients; set => clients = value; }
        public List<Chambre> Chambres { get => chambres; set => chambres = value; }
        public List<Reservation> Reservations { get { return reservations; } set => reservations = value; }

        private Sauvegarde sauvegarde;
        public Hotel(string nom)
        {
            Nom = nom;
            sauvegarde = new Sauvegarde(nom);
            //Clients = new List<Client>();
            //Récupérer les clients par l'objet sauvegarde
            Clients = sauvegarde.LireClients();
            Reservations = sauvegarde.LireReservations();
            Chambres = sauvegarde.LireChambres();
            if (Chambres.Count == 0)
                GenerationAleatoireChambres();
            else
                Chambres.ForEach(c => c.ChangeStatut += NotificationService.Chambre_ChangeStatut);
        }

        private void GenerationAleatoireChambres()
        {
            //Génerer 20 chambres par exemple
            Random rPrix = new Random();
            Random rOccup = new Random();
            for(int i=1; i <= 20; i++)
            {
                Chambre chambre = new Chambre()
                {
                    Tarif = rPrix.Next(30, 40),
                    NbOccp = rOccup.Next(2, 5)
                };
                //Ecouter event changeStatut de chaque chambre
                chambre.ChangeStatut += NotificationService.Chambre_ChangeStatut;
                Chambres.Add(chambre);
            }
            //Ajouter les chambres dans le fichier
            sauvegarde.EcrireChambres(Chambres);
        }

        public Client CreerClient(string nom, string prenom, string telephone)
        {
            //Création Client
            //On verifie si un client existe avec le même numéro de téléphone
            Client c = RechercherClient(telephone);
            if(c == null)
            {
                Client client = new Client() { Nom = nom, Prenom = prenom, Telephone = telephone };
                Clients.Add(client);
                //Ajouter le client dans le fichier
                sauvegarde.EcrireClients(client);
                return client;
            }
            return null;
        }
        public Client RechercherClient(string telephone)
        {
            //Recherche client par téléphone
            return Clients.Find(c => c.Telephone == telephone);
        }

        public Reservation AjouterReservation(Client client, int nbreOccup)
        {
            if(VerifierSiReservationPossible(nbreOccup))
            {
                //Création Réservation
                Reservation reservation = new Reservation();
                reservation.Client = client;
                int count = 0;
                foreach (Chambre chambre in Chambres)
                {
                    if (chambre.Statut == StatutChambre.Libre)
                    {
                        reservation.Chambres.Add(chambre);
                        count += chambre.NbOccp;
                        chambre.Statut = StatutChambre.Occupe;
                        if (count >= nbreOccup)
                        {
                            break;
                        }
                    }
                }
                //Ajout de la réservation créée dans la liste des réservations de l'hôtel
                Reservations.Add(reservation);

                //Ajouter la réservation dans le fichier csv
                sauvegarde.EcrireReservations(reservation);

                //Mettre à jour le fichier des chambres, à cause du changement de statut chambre
                sauvegarde.EcrireChambres(Chambres);
                return reservation;
            }
            return null;
        }

        public bool VerifierSiReservationPossible(int nbOccup)
        {
            bool retour = false;
            int count = 0;
            foreach(Chambre c in Chambres)
            {
                if(c.Statut == StatutChambre.Libre)
                {
                    count += c.NbOccp;
                    if(count >= nbOccup)
                    {
                        retour = true;
                        break;
                    }
                }
            }
            return retour;
        }
        

        public List<Reservation> RechercherReservationsParClient(string telephone)
        {
            //Recherche des réservations par numéro de téléphone client
            //List<Reservation> reservations = new List<Reservation>();
            //foreach(Reservation r in Reservations)
            //{
            //    if(r.Client.Telephone == telephone)
            //    {
            //        reservations.Add(r);
            //    }
            //}
            //return reservations;
            //=> Avec une expression lambda
            //return Reservations.Where(r => r.Client.Telephone == telephone).ToList();
            return Reservations.FindAll(r => r.Client.Telephone == telephone);
        }

        public Reservation RechercheReservationParNumero(int numero)
        {
            //Recherche d'une réservation par numéro
            return Reservations.Find(r => r.Numero == numero) ;
        }

        public bool AnnulerReservation(Reservation reservation)
        {
            reservation.AnnulerReservation();
            //Mise à jour des chambres avec nouveau statut
            foreach(Chambre chambre in reservation.Chambres)
            {
                Chambre chambreHotel = Chambres.Find(c => c.Numero == chambre.Numero);
                chambreHotel.UpdateStatut(StatutChambre.Libre);
            }
            sauvegarde.EcrireChambres(Chambres);
            //Mise à jour des réservations
            sauvegarde.MiseAjourReservations(Reservations);
            return true;
        }

    }
}
