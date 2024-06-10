using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace HotKeys_Taps
{
    public partial class Form1 : Form
    {
        private KeyboardHook keyHook = new KeyboardHook();
        public Form1()
        {
            InitializeComponent();
            keyHook.HookedKeys.Add(Keys.A);
            keyHook.KeyDown += new KeyEventHandler(gkh_KeyDown);
            keyHook.KeyUp += new KeyEventHandler(gkh_KeyUp);
        }
        void gkh_KeyUp(object sender, KeyEventArgs e)
        {
            e.Handled = true;
        }
        void gkh_KeyDown(object sender, KeyEventArgs e)
        {
            MessageBox.Show("HAS PULSADO LA TECLA A", "TECLA PULSADA", MessageBoxButtons.OK, MessageBoxIcon.Exclamation) ;
            e.Handled = true;
        }
    }
}
