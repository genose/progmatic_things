using Hotel.Classes;
using System;
using System.Collections.Generic;
using System.Text;

namespace Hotel.Tools
{
    class NotificationService
    {
        public static void Chambre_ChangeStatut(int numero, StatutChambre statut)
        {
            Console.WriteLine("La chambre numéro {0} a un nouveau statut {1}", numero, statut);
        }
    }
}
