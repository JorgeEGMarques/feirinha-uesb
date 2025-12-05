"use client"

import { useEffect, useState } from 'react';
import { User, Store, X } from 'lucide-react'; // Adicionei o ícone 'X' para fechar o modal
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/auth-store';
import { useRouter } from 'next/navigation';
import { product, profile, stock, tent } from '@/utils/types';

// Interface auxiliar para os dados do formulário
interface NewTentForm {
  name: string;
  userLicense: string;
  cpfHolder: string;
}

export default function Profile() {
  const router = useRouter();
  const { logout } = useAuthStore();

  const [userProfile, setUserProfile] = useState<any>(null);
  const [userTents, setUserTents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // Estados para o Modal e Formulário
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [error, setError] = useState('');
  const [newTentData, setNewTentData] = useState<NewTentForm>({
    name: '',
    userLicense: '',
    cpfHolder: ''
  });

  useEffect(() => {
    // Adicionei verificação para evitar erro se o localStorage estiver vazio
    const storedData = localStorage.getItem("userData");
    if (storedData) {
      const { user, tents } = JSON.parse(storedData);
      setUserProfile(user);
      setUserTents(tents || []);
      
      // Pré-preenche o CPF do responsável com o CPF do usuário logado (opcional)
      if(user?.cpf) {
        setNewTentData(prev => ({...prev, cpfHolder: user.cpf}));
      }
    }
    setLoading(false);
  }, []);

  const handleLogout = () => {
    logout();
    router.push('/');
  };

  // Abre o modal
  const handleNewTent = () => {
    setError('');
    setIsModalOpen(true);
  };

  // Salva a nova barraca
  const handleSaveTent = async (e: React.FormEvent) => {
    e.preventDefault();

    // 1. Validação: Verifica campos em branco
    if (!newTentData.name.trim() || !newTentData.userLicense.trim() || !newTentData.cpfHolder.trim()) {
      setError('Por favor, preencha todos os campos.');
      return;
    }

    await fetch(`${process.env.NGROK_URL}/tents`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: newTentData.name,
        userLicense: newTentData.userLicense,
        cpfHolder: newTentData.cpfHolder,
      })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json(); // Parse the JSON response from the server
    })
    .then(data => console.log("success", data))
    .catch(error => console.log('Error', error));

    // 2. Cria o objeto da nova barraca
    const newTent: tent = {
      code: Math.floor(Math.random() * 10000), // Gera um código aleatório para exemplo
      name: newTentData.name,
      userLicense: newTentData.userLicense,
      cpfHolder: newTentData.cpfHolder,
      items: [] // Inicia sem produtos
    };

    // 3. Atualiza o estado local
    const updatedTents = [...userTents, newTent];
    setUserTents(updatedTents);

    // 4. Atualiza o LocalStorage para persistir os dados
    const storedData = localStorage.getItem("userData");
    if (storedData) {
        const parsedData = JSON.parse(storedData);
        parsedData.tents = updatedTents;
        localStorage.setItem("userData", JSON.stringify(parsedData));
    }

    // 5. Limpa e fecha
    setIsModalOpen(false);
    setNewTentData({ name: '', userLicense: '', cpfHolder: userProfile?.cpf || '' });
    setError('');
  };

  if (loading) return <div className="flex h-screen items-center justify-center">Carregando perfil...</div>;

  return (
    <div className="min-h-screen bg-gray-50 p-4 sm:p-8 flex justify-center relative">
      <div className="w-full max-w-4xl space-y-8">

        {/* --- Cartão de Perfil Principal --- */}
        <div className="bg-white p-8 rounded-xl shadow-2xl border border-gray-100 flex flex-col md:flex-row items-center md:items-start space-y-6 md:space-y-0 md:space-x-8">
          
          {/* Foto do Perfil */}
          <div className="flex-shrink-0">
              <div className="h-32 w-32 rounded-full bg-gray-200 flex items-center justify-center">
                <span className="text-gray-500">Sem Foto</span>
              </div>
          </div>

          {/* Detalhes do Usuário */}
          <div className="text-center md:text-left flex-grow">
            <div className="flex items-center justify-center md:justify-start">
              <User className="w-6 h-6 text-indigo-600 mr-2" />
              <h1 className="text-3xl font-extrabold text-gray-900">{userProfile?.nome}</h1>
            </div>
            <p className="mt-1 text-lg text-indigo-600 font-medium">{userProfile?.email}</p>
            <p className="mt-2 text-sm text-gray-500">
                Gerenciador principal das barracas no mercado.
            </p>

            <div className='flex flex-row justify-center md:justify-start gap-4 mt-4'>
              <Button
                className="bg-indigo-600 hover:bg-indigo-500 transition-colors"
                onClick={handleNewTent}
              >
                Adicionar Barraca
              </Button>
              <Button
                variant="destructive"
                className="bg-red-500 hover:bg-red-600 transition-colors"
                onClick={handleLogout}
              >
                Logout
              </Button>
            </div>
          </div>
        </div>

        {/* --- Lista de Barracas --- */}
        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-gray-800 flex items-center">
            <Store className="w-6 h-6 text-pink-500 mr-2" />
            Minhas Barracas ({userTents.length})
          </h2>
          
          {userTents.length === 0 ? (
            <p className="text-gray-500 italic">Nenhuma barraca cadastrada ainda.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {userTents.map((t: tent) => (
                <div
                  key={t.code}
                  className="bg-white p-6 rounded-xl shadow-lg border-t-4 border-pink-500 transition duration-300 hover:shadow-xl hover:border-pink-600"
                >
                  <div className='flex justify-between items-center mb-4'>
                    <h3 className="text-xl font-bold text-gray-800 flex items-center">{t.name}</h3>
                    <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">Licença: {t.userLicense}</span>
                  </div>
                  
                  {(!t.items || t.items.length === 0) ? (
                     <p className="text-sm text-gray-400">Nenhum produto em estoque.</p>
                  ) : (
                    t.items.map((s: stock) => (
                        <div key={s.productCode} className="mb-4 border-b pb-2 last:border-0">
                        <div className="flex items-center mb-1">
                            <Store className="w-4 h-4 text-pink-500 mr-2" />
                            <h4 className="font-semibold text-gray-900">{s.product.name}</h4>
                        </div>
                        <p className="text-gray-600 text-xs italic">Estoque: {s.stockQuantity}</p>
                        </div>
                    ))
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* --- MODAL DE FORMULÁRIO --- */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden transform transition-all scale-100">
            
            {/* Cabeçalho do Modal */}
            <div className="bg-indigo-600 px-6 py-4 flex justify-between items-center">
              <h3 className="text-lg font-bold text-white flex items-center">
                <Store className="w-5 h-5 mr-2" /> Nova Barraca
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)} 
                className="text-white hover:text-gray-200 transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Corpo do Formulário */}
            <form onSubmit={handleSaveTent} className="p-6 space-y-4">
              
              {/* Mensagem de Erro */}
              {error && (
                <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm border border-red-200">
                  {error}
                </div>
              )}

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Nome da Barraca</label>
                <input
                  type="text"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  placeholder="Ex: Barraca do Zé"
                  value={newTentData.name}
                  onChange={(e) => setNewTentData({...newTentData, name: e.target.value})}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Número da Licença/Alvará</label>
                <input
                  type="text"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  placeholder="Ex: LIC-2024-001"
                  value={newTentData.userLicense}
                  onChange={(e) => setNewTentData({...newTentData, userLicense: e.target.value})}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">CPF do Responsável</label>
                <input
                  type="text"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-gray-50"
                  placeholder="000.000.000-00"
                  value={newTentData.cpfHolder}
                  onChange={(e) => setNewTentData({...newTentData, cpfHolder: e.target.value})}
                />
              </div>

              <div className="flex justify-end space-x-3 mt-6">
                <Button 
                  type="button" 
                  variant="outline" 
                  onClick={() => setIsModalOpen(false)}
                >
                  Cancelar
                </Button>
                <Button 
                  type="submit" 
                  className="bg-indigo-600 hover:bg-indigo-500"
                >
                  Confirmar Cadastro
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
}