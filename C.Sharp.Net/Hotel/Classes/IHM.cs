using System;
using System.Collections.Generic;
using System.Text;

namespace Hotel.Classes
{
    public class IHM
    {
        Hotel hotel;
        public void Start()
        {
            CreationHotel();
            string choix;
            do
            {
                Menu();
                choix = Console.ReadLine();
                Console.Clear();
                switch(choix)
                {
                    case "1":
                        ActionAjoutClient();
                        break;
                    case "2":
                        ActionListeClients();
                        break;
                    case "3":
                        ActionListeReservationsClient();
                        break;
                    case "4":
                        ActionAjoutReservation();
                        break;
                    case "5":
                        ActionAnnulerReservation();
                        break;
                    case "6":
                        ActionListeReservations();
                        break;
                    case "0":
                        Environment.Exit(0);
                        break;
                }
            } while (choix != "0");
        }

        private void CreationHotel()
        {
            Console.Write("Le nom de l'hôtel : ");
            string nom = Console.ReadLine();
            hotel = new Hotel(nom);
            Console.Clear();
        }

        private void Menu()
        {
            Console.WriteLine("1----Ajouter un client");
            Console.WriteLine("2----Afficher la liste des clients");
            Console.WriteLine("3----Afficher les réservations d'un client");
            Console.WriteLine("4----Ajouter une réservation");
            Console.WriteLine("5----Annuler une réservation");
            Console.WriteLine("6----Afficher la liste des réservations");
            Console.WriteLine("0----Quitter");
        }

        private void ActionAjoutClient()
        {
            Console.Write("Merci de saisir le nom du client : ");
            string nom = Console.ReadLine();
            Console.Write("Merci de saisir le prénom du client : ");
            string prenom = Console.ReadLine();
            Console.Write("Merci de saisir le téléphone : ");
            string telephone = Console.ReadLine();
            Client client = hotel.CreerClient(nom, prenom, telephone);
            if(client == null)
            {
                AfficherErreur("Erreur création client");
            }
            else
            {
                AfficherSuccess(client.ToString());
            }
        }

        private void ActionListeClients()
        {
            Console.WriteLine("=====Liste clients=====");
            hotel.Clients.ForEach(c =>
            {
                Console.WriteLine(c.ToString());
            });
            ClearConsole();
        }
        private void ActionListeReservationsClient()
        {
            Console.Write("Merci de saisir le numéro de téléphone : ");
            string telephone = Console.ReadLine();
            List<Reservation> reservations = hotel.RechercherReservationsParClient(telephone);
            if(reservations.Count == 0)
            {
                AfficherErreur("Aucune réservation pour ce client");
            }
            else
            {
                Console.WriteLine("=====Liste réservations=====");
                reservations.ForEach(r =>
                {
                    Console.WriteLine(r.ToString());
                });
                ClearConsole();
            }
        }

        private void ActionAjoutReservation()
        {
            Console.Write("Merci de saisir le numéro de téléphone : ");
            string telephone = Console.ReadLine();
            Client client = hotel.RechercherClient(telephone);
            if(client == null)
            {
                AfficherErreur("Aucun client avec ce numéro");
            }
            else
            {
                Console.Write("Nombre d'occupant : ");
                int nb = Convert.ToInt32(Console.ReadLine());
                Reservation reservation = hotel.AjouterReservation(client, nb);
                if(reservation == null)
                {
                    AfficherErreur("Erreur création réservation");
                }
                else
                {
                    AfficherSuccess(reservation.ToString());
                }
            }
        }

        private void ActionAnnulerReservation()
        {
            Console.Write("Merci de saisir le numéro de réservation : ");
            int numero = Convert.ToInt32(Console.ReadLine());
            Reservation reservation = hotel.RechercheReservationParNumero(numero);
            if(reservation == null)
            {
                AfficherErreur("Aucune réservation avec ce numéro");
            }
            else
            {
                //reservation.AnnulerReservation();
                hotel.AnnulerReservation(reservation);
                AfficherSuccess("Réservation annulée");
            }
        }
        private void ActionListeReservations()
        {
            Console.WriteLine("=====Liste réservations=====");
            hotel.Reservations.ForEach(r =>
            {
                Console.WriteLine(r.ToString());
            });
            ClearConsole();
        }

        private void AfficherErreur(string erreur)
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine(erreur);
            Console.ForegroundColor = ConsoleColor.White;
            ClearConsole();
        }
        private void AfficherSuccess(string message)
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine(message);
            Console.ForegroundColor = ConsoleColor.White;
            ClearConsole();
        }

        private void ClearConsole()
        {
            Console.WriteLine("Une touche pour continuer...");
            Console.ReadLine();
            Console.Clear();
        }

        
    }
}
