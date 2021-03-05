using Hotel.Classes;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace Hotel.Tools
{
    class Sauvegarde
    {
        private StreamWriter writer;
        private StreamReader reader;

        private string pathClient = "clients.csv";
        private string pathReservation = "reservations.csv";
        private string pathChambre = "chambres.csv";
        private string pathReservationsChambres = "reservations-chambres.csv";

        private string nomHotel;

        public Sauvegarde(string nom)
        {
            nomHotel = nom;
        }
        public List<Client> LireClients()
        {
            //Récupérer les clients du fichier clients
            List<Client> listes = new List<Client>();
            if(File.Exists($"{nomHotel}-{pathClient}"))
            {
                reader = new StreamReader($"{nomHotel}-{pathClient}");
                string ligne = reader.ReadLine();
                while(ligne != null)
                {
                    string[] ligneTab = ligne.Split(';');
                    Client client = new Client { Nom = ligneTab[0], Prenom = ligneTab[1], Telephone = ligneTab[2] };
                    listes.Add(client);
                    ligne = reader.ReadLine();
                }
                reader.Close();
            }
            return listes;
        }
        public void EcrireClients(Client client)
        {
            writer = new StreamWriter($"{nomHotel}-{pathClient}", true);
            writer.WriteLine($"{client.Nom};{client.Prenom};{client.Telephone}");
            writer.Close();
        }

        public List<Chambre> LireChambres()
        {
            List<Chambre> listes = new List<Chambre>();
            if (File.Exists($"{nomHotel}-{pathChambre}"))
            {
                reader = new StreamReader($"{nomHotel}-{pathChambre}");
                string ligne = reader.ReadLine();
                while (ligne != null)
                {
                    string[] ligneTab = ligne.Split(';');
                    Chambre chambre= new Chambre
                    {
                        Numero = Convert.ToInt32(ligneTab[0]),
                        Tarif = Convert.ToDecimal(ligneTab[1]),
                        NbOccp = Convert.ToInt32(ligneTab[2]),
                        //Statut = (StatutChambre)Convert.ToInt32(ligneTab[3])
                        //Parse de l'enum permet de convertir une chaine de caractère en valeur de enum
                        //Statut = (StatutChambre)Enum.Parse(typeof(StatutChambre), ligneTab[3])
                        Statut = Extension.ConvertEnum<StatutChambre>(ligneTab[3])
                    };
                    listes.Add(chambre);
                    ligne = reader.ReadLine();
                }
                reader.Close();
            }
            return listes;
        }

        public void EcrireChambres(List<Chambre> chambres)
        {
            writer = new StreamWriter($"{nomHotel}-{pathChambre}");
            foreach(Chambre c in chambres)
            {
                writer.WriteLine($"{c.Numero};{c.Tarif};{c.NbOccp};{c.Statut}");
            }
            writer.Close();
        }

        public List<Reservation> LireReservations()
        {
            List<Client> listeClients = LireClients();

            //On récupère dans un premier temps le numéro, statut et client (à l'aide du téléphone client) du premier fichier reservations.csv
            List<Reservation> listes = new List<Reservation>();
            if (File.Exists($"{nomHotel}-{pathChambre}"))
            {
                reader = new StreamReader($"{nomHotel}-{pathReservation}");
                string ligne = reader.ReadLine();
                while (ligne != null)
                {
                    string[] ligneTab = ligne.Split(';');
                    Reservation reservation = new Reservation
                    {
                        Numero = Convert.ToInt32(ligneTab[0]),
                        Statut = Extension.ConvertEnum<StatutReservation>(ligneTab[1]),
                        Client = listeClients.Find(c => c.Telephone ==  ligneTab[2])
                    };
                    listes.Add(reservation);
                    ligne = reader.ReadLine();
                }
                reader.Close();
            }

            //à l'aide de la méthode lireChambreReservation, on récupère les chambres de chaque réservation
            foreach(Reservation r in listes)
            {
                LireChambresReservation(r);
            }
            return listes;
        }


        private void LireChambresReservation(Reservation reservation)
        {
            List<Chambre> listeChambres = LireChambres();
            if (File.Exists($"{nomHotel}-{pathReservationsChambres}"))
            {
                reader = new StreamReader($"{nomHotel}-{pathReservationsChambres}");
                string ligne = reader.ReadLine();
                while (ligne != null)
                {
                    string[] ligneTab = ligne.Split(';');
                    if(Convert.ToInt32(ligneTab[0]) == reservation.Numero)
                    {
                        Chambre chambre = listeChambres.Find(c => c.Numero == Convert.ToInt32(ligneTab[1]));
                        reservation.Chambres.Add(chambre);
                    }
                    ligne = reader.ReadLine();
                }
                reader.Close();
            }
        }
       

        public void EcrireReservations(Reservation reservation)
        {
            //On commence par ecrire, dans un premier temps, dans le fichier reservation(numero, statut, telephoneClient)
            writer = new StreamWriter($"{nomHotel}-{pathReservation}", append:true);
            writer.WriteLine($"{reservation.Numero};{reservation.Statut};{reservation.Client.Telephone}");
            writer.Close();
            //Ensuite on sauvegarde le numéro de reservation et les numéro de chambres dans un deuxième fichier reservations-chambres
            EcrireReservationChambres(reservation);
        }

        public void MiseAjourReservations(List<Reservation> reservations)
        {
            writer = new StreamWriter($"{nomHotel}-{pathReservation}");
            foreach(Reservation reservation in reservations)
            {
                writer.WriteLine($"{reservation.Numero};{reservation.Statut};{reservation.Client.Telephone}");
            }
            writer.Close();
        }

        private void EcrireReservationChambres(Reservation reservation)
        {
            writer = new StreamWriter($"{nomHotel}-{pathReservationsChambres}", append:true);
            foreach(Chambre c in reservation.Chambres)
            {
                writer.WriteLine($"{reservation.Numero};{c.Numero}");
            }
            writer.Close();
        }
        
    }
}
