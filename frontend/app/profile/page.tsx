"use client"

import { useEffect, useState } from 'react';
import { User, Store, X, Pencil, Plus, Save } from 'lucide-react'; // Novos ícones importados
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/auth-store';
import { useRouter } from 'next/navigation';
import { product, profile, stock, tent } from '@/utils/types';

interface NewTentForm {
  name: string;
  userLicense: string;
  cpfHolder: string;
}

export default function Profile() {
  const router = useRouter();
  const { logout } = useAuthStore();

  const [userProfile, setUserProfile] = useState<any>(null);
  const [userTents, setUserTents] = useState<tent[]>([]); // Tipagem corrigida para array de tents
  const [loading, setLoading] = useState(true);

  const [isModalOpen, setIsModalOpen] = useState(false); // Modal de Criar Barraca
  const [editingTent, setEditingTent] = useState<tent | null>(null); // Modal de Editar Barraca (Armazena a barraca atual)
  
  const [error, setError] = useState('');
  
  const [newTentData, setNewTentData] = useState<NewTentForm>({
    name: '',
    userLicense: '',
    cpfHolder: ''
  });

  useEffect(() => {
    const storedData = localStorage.getItem("userData");
    if (storedData) {
      const { user, tents } = JSON.parse(storedData);
      setUserProfile(user);
      setUserTents(tents || []);
      
      if(user?.cpf) {
        setNewTentData(prev => ({...prev, cpfHolder: user.cpf}));
      }
    }
    setLoading(false);
  }, []);

  const updateLocalStorage = (tents: tent[]) => {
    setUserTents(tents);

    const storedData = localStorage.getItem("userData");
    if (storedData) {
        const parsedData = JSON.parse(storedData);
        parsedData.tents = tents;
        localStorage.setItem("userData", JSON.stringify(parsedData));
    }
  };

  const handleLogout = () => {
    logout();
    router.push('/');
  };

  const handleNewTent = () => {
    setError('');
    setIsModalOpen(true);
  };

  const handleSaveTent = async (e: React.FormEvent) => {
    e.preventDefault();

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
      return response.json();
    })
    .then(data => console.log("success", data))
    .catch(error => console.log('Error', error));

    const newTent: tent = {
      code: Math.floor(Math.random() * 10000),
      name: newTentData.name,
      userLicense: newTentData.userLicense,
      cpfHolder: newTentData.cpfHolder,
      items: [] 
    };

    const updatedTents = [...userTents, newTent];
    updateLocalStorage(updatedTents);
    
    setIsModalOpen(false);
    setNewTentData({ name: '', userLicense: '', cpfHolder: userProfile?.cpf || '' });
    setError('');
  };

  const handleOpenEdit = (t: tent) => {
    // Fazemos uma cópia profunda para não alterar o estado visual antes de salvar
    setEditingTent(JSON.parse(JSON.stringify(t))); 
  };

  // 2. Adiciona um novo item vazio à lista de itens da barraca em edição
  const handleAddNewItem = () => {
    if (!editingTent) return;
    
    const newProduct: any = {
        name: "Novo Item",
        description: "Descrição do item",
        price: 0,
        imagem: null
    };

    const newStockItem: any = {
        tentCode: editingTent.code,
        stockQuantity: 1,
        product: newProduct
    };

    setEditingTent({
      ...editingTent,
      items: [...editingTent.items, newStockItem]
    });
  };

  // 3. Atualiza campos de um item existente (Nome, Descrição, Preço ou Estoque)
  const handleUpdateItem = (index: number, field: string, value: string | number) => {
    if (!editingTent) return;

    const updatedItems = [...editingTent.items];
    const itemToUpdate = updatedItems[index];

    if (field === 'stockQuantity') {
        // Atualiza a quantidade no objeto Stock
        itemToUpdate.stockQuantity = Number(value);
    } else {
        // Atualiza propriedades dentro do objeto Product (Nome, Preço, Descrição)
        (itemToUpdate.product as any)[field] = value;
    }

    setEditingTent({ ...editingTent, items: updatedItems });
  };

  // 4. Salva as alterações da edição no estado principal e no LocalStorage
  const handleSaveEdits = async () => {
    if (!editingTent) return;

    try {
      // Faz a requisição PUT para atualizar a barraca específica no backend
      const response = await fetch(`https://anja-superethical-appeasedly.ngrok-free.dev/crud/api/tents/${editingTent.code}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'ngrok-skip-browser-warning': 'true', // Cabeçalho importante para evitar tela de erro do ngrok
        },
        body: JSON.stringify(editingTent) // Envia o objeto da barraca completo com os itens atualizados
      });

      if (!response.ok) {
        throw new Error(`Erro na API: ${response.status}`);
      }

      // Se a API respondeu OK, atualizamos o estado local e o localStorage
      const updatedTents = userTents.map(t => 
          t.code === editingTent.code ? editingTent : t
      );

      updateLocalStorage(updatedTents);
      setEditingTent(null); // Fecha o modal
      console.log("Barraca atualizada com sucesso!");

    } catch (error) {
      console.error("Erro ao salvar alterações:", error);
      alert("Houve um erro ao salvar as alterações. Verifique sua conexão.");
    }
  };


  if (loading) return <div className="flex h-screen items-center justify-center">Carregando perfil...</div>;

  return (
    <div className="min-h-screen bg-gray-50 p-4 sm:p-8 flex justify-center relative">
      <div className="w-full max-w-4xl space-y-8">

        {/* --- Cartão de Perfil --- */}
        <div className="bg-white p-8 rounded-xl shadow-2xl border border-gray-100 flex flex-col md:flex-row items-center md:items-start space-y-6 md:space-y-0 md:space-x-8">
          <div className="flex-shrink-0">
              <div className="h-32 w-32 rounded-full bg-gray-200 flex items-center justify-center">
                <span className="text-gray-500">Sem Foto</span>
              </div>
          </div>
          <div className="text-center md:text-left flex-grow">
            <h1 className="text-3xl font-extrabold text-gray-900 flex items-center justify-center md:justify-start">
                 <User className="w-6 h-6 text-indigo-600 mr-2" /> {userProfile?.nome}
            </h1>
            <p className="mt-1 text-lg text-indigo-600 font-medium">{userProfile?.email}</p>
            <div className='flex flex-row justify-center md:justify-start gap-4 mt-4'>
              <Button onClick={handleNewTent} className="bg-indigo-600 hover:bg-indigo-500">Adicionar Barraca</Button>
              <Button onClick={handleLogout} variant="destructive">Logout</Button>
            </div>
          </div>
        </div>

        {/* --- Lista de Barracas --- */}
        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-gray-800 flex items-center">
            <Store className="w-6 h-6 text-pink-500 mr-2" /> Minhas Barracas ({userTents.length})
          </h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {userTents.map((t: tent) => (
              <div key={t.code} className="bg-white p-6 rounded-xl shadow-lg border-t-4 border-pink-500 relative hover:shadow-xl transition">
                
                {/* Cabeçalho do Card da Barraca */}
                <div className='flex justify-between items-start mb-4'>
                  <div>
                    <h3 className="text-xl font-bold text-gray-800">{t.name}</h3>
                    <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">Licença: {t.userLicense}</span>
                  </div>
                  {/* Botão de Editar Barraca */}
                  <Button 
                    variant="ghost" 
                    size="sm" 
                    onClick={() => handleOpenEdit(t)}
                    className="text-gray-500 hover:text-indigo-600 hover:bg-indigo-50"
                  >
                    <Pencil className="w-5 h-5" />
                  </Button>
                </div>
                
                {/* Listagem Resumida de Itens */}
                {(!t.items || t.items.length === 0) ? (
                   <p className="text-sm text-gray-400">Nenhum produto cadastrado.</p>
                ) : (
                  <div className="space-y-3">
                     {t.items.slice(0, 3).map((s: stock) => ( // Mostra apenas os 3 primeiros
                      <div key={s.productCode} className="flex justify-between text-sm border-b border-gray-100 pb-1">
                          <span className="text-gray-700 font-medium">{s.product.name}</span>
                          <span className="text-gray-500">Qtd: {s.stockQuantity}</span>
                      </div>
                     ))}
                     {t.items.length > 3 && <p className="text-xs text-center text-gray-400">e mais {t.items.length - 3} itens...</p>}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* --- MODAL DE CRIAR NOVA BARRACA --- */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-md p-6">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-bold">Nova Barraca</h3>
                <button onClick={() => setIsModalOpen(false)}><X className="w-6 h-6 text-gray-500" /></button>
            </div>
            <form onSubmit={handleSaveTent} className="space-y-4">
              {error && <div className="text-red-600 text-sm">{error}</div>}
              <input type="text" className="w-full p-2 border rounded" placeholder="Nome da Barraca" value={newTentData.name} onChange={(e) => setNewTentData({...newTentData, name: e.target.value})} />
              <input type="text" className="w-full p-2 border rounded" placeholder="Licença" value={newTentData.userLicense} onChange={(e) => setNewTentData({...newTentData, userLicense: e.target.value})} />
              <input type="text" className="w-full p-2 border rounded" placeholder="CPF Responsável" value={newTentData.cpfHolder} onChange={(e) => setNewTentData({...newTentData, cpfHolder: e.target.value})} />
              <Button type="submit" className="w-full bg-indigo-600">Criar Barraca</Button>
            </form>
          </div>
        </div>
      )}

      {/* --- MODAL DE EDITAR BARRACA (Gerenciar Itens) --- */}
      {editingTent && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-4xl max-h-[90vh] flex flex-col overflow-hidden">
            
            {/* Header do Modal */}
            <div className="bg-indigo-600 px-6 py-4 flex justify-between items-center flex-shrink-0">
              <h3 className="text-lg font-bold text-white flex items-center">
                <Pencil className="w-5 h-5 mr-2" /> Editando: {editingTent.name}
              </h3>
              <button onClick={() => setEditingTent(null)} className="text-white hover:text-gray-200">
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Corpo com Scroll */}
            <div className="p-6 overflow-y-auto flex-grow bg-gray-50">
                
                <div className="flex justify-between items-center mb-4">
                    <h4 className="font-semibold text-gray-700">Itens e Estoque</h4>
                    <Button onClick={handleAddNewItem} size="sm" className="bg-green-600 hover:bg-green-500">
                        <Plus className="w-4 h-4 mr-1" /> Novo Item
                    </Button>
                </div>

                {editingTent.items.length === 0 ? (
                    <div className="text-center py-8 text-gray-500 border-2 border-dashed rounded-lg">
                        Nenhum item nesta barraca. Adicione um novo item acima.
                    </div>
                ) : (
                    <div className="space-y-4">
                        {editingTent.items.map((item, index) => (
                            <div key={item.productCode} className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 grid grid-cols-1 md:grid-cols-12 gap-4 items-start">
                                
                                {/* Coluna 1: Dados Básicos */}
                                <div className="md:col-span-4 space-y-2">
                                    <label className="text-xs font-bold text-gray-500 uppercase">Nome do Produto</label>
                                    <input 
                                        type="text" 
                                        className="w-full p-2 border rounded text-sm font-semibold"
                                        value={item.product.name}
                                        onChange={(e) => handleUpdateItem(index, 'name', e.target.value)}
                                        placeholder="Nome do Item"
                                    />
                                </div>

                                {/* Coluna 2: Descrição */}
                                <div className="md:col-span-4 space-y-2">
                                    <label className="text-xs font-bold text-gray-500 uppercase">Descrição</label>
                                    <textarea 
                                        rows={1}
                                        className="w-full p-2 border rounded text-sm"
                                        value={item.product.description}
                                        onChange={(e) => handleUpdateItem(index, 'description', e.target.value)}
                                        placeholder="Descrição breve"
                                    />
                                </div>

                                {/* Coluna 3: Preço e Estoque */}
                                <div className="md:col-span-2 space-y-2">
                                    <label className="text-xs font-bold text-gray-500 uppercase">Preço (R$)</label>
                                    <input 
                                        type="number" 
                                        className="w-full p-2 border rounded text-sm"
                                        value={item.product.price}
                                        onChange={(e) => handleUpdateItem(index, 'price', parseFloat(e.target.value))}
                                        placeholder="0.00"
                                    />
                                </div>

                                <div className="md:col-span-2 space-y-2">
                                    <label className="text-xs font-bold text-gray-500 uppercase">Estoque</label>
                                    <input 
                                        type="number" 
                                        className="w-full p-2 border rounded text-sm bg-indigo-50 font-bold text-indigo-700"
                                        value={item.stockQuantity}
                                        onChange={(e) => handleUpdateItem(index, 'stockQuantity', parseInt(e.target.value))}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Footer Fixo */}
            <div className="p-4 bg-white border-t flex justify-end space-x-3 flex-shrink-0">
                <Button variant="outline" onClick={() => setEditingTent(null)}>Cancelar</Button>
                <Button onClick={handleSaveEdits} className="bg-indigo-600 hover:bg-indigo-500">
                    <Save className="w-4 h-4 mr-2" /> Salvar Alterações
                </Button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}