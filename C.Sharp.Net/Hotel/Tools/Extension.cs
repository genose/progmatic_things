using System;
using System.Collections.Generic;
using System.Text;

namespace Hotel.Tools
{
    class Extension
    {

        public static T ConvertEnum<T>(string value)
        {
            return (T)Enum.Parse(typeof(T), value);
        }
    }
}
