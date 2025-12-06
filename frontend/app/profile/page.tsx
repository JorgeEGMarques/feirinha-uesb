"use client"

import { useEffect, useState } from 'react';
import { User, Store, ShoppingBag, DollarSign, Calendar } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/auth-store';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

// --- Interfaces ---

interface Item {
  id: string;
  estoque: number;
}

interface Barraca {
  cod_barraca: string;
  cpf_dono: string;
  nome_barraca: string;
  licensa_usuario: string;
  lista_de_itens: Item[];
}

interface UserProfile {
  id: string;
  login: string;
  password: string;
  name: string;
  cpf: string;
  src: string;
}

interface Produto {
  id: string | number;
  src: string;
  name: string;
  description: string;
  price: number;
}

// Interfaces de Venda
interface SaleItem {
    productCode: number;
    saleQuantity: number;
    salePrice: number;
}

interface Sale {
    id: number;
    saleDate: string; // Vem como string ISO do JSON (LocalDate)
    tentCode: number;
    userCode: string;
    items: SaleItem[];
}

export default function Profile() {
    const router = useRouter();
    const { logout } = useAuthStore();

    const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
    const [userTents, setUserTents] = useState<any[]>([]);
    const [purchaseHistory, setPurchaseHistory] = useState<Sale[]>([]); // Histórico de Compras
    const [salesHistory, setSalesHistory] = useState<Sale[]>([]);       // Histórico de Vendas (minhas barracas)
    const [productsMap, setProductsMap] = useState<Map<any, Produto>>(new Map()); // Para acesso rápido aos nomes
    
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      const fetchData = async () => {
        try {
          const userId = localStorage.getItem("userId");

          if (!userId) {
            console.log("Usuário não logado");
            return;
          }

          // Busca todos os dados necessários
          const [profilesRes, tentsRes, productsRes, salesRes] = await Promise.all([
            fetch('http://localhost:3000/profile').then(res => res.json()),
            fetch('http://localhost:3000/tents').then(res => res.json()),
            fetch('http://localhost:3000/products').then(res => res.json()),
            // Se estiver usando o backend Java, pode usar os filtros específicos:
            // fetch(`http://localhost:8080/crud/api/sales?userId=${userId}`)
            // Aqui buscamos 'sales' genérico para filtrar no front por compatibilidade com mock
            fetch('http://localhost:3000/sales').then(res => res.ok ? res.json() : []) 
          ]);
          
          // 1. Configurar Usuário
          const user = profilesRes.find((a: UserProfile) => a.id === userId);
          if (!user) return;
          setUserProfile(user);

          // 2. Mapa de Produtos (ID -> Objeto) para acesso rápido
          const mapa = new Map(productsRes.map((p: Produto) => [String(p.id), p]));
          setProductsMap(mapa);

          // 3. Configurar Barracas do Usuário
          let tents = tentsRes.filter((a: Barraca) => a.cpf_dono === user.cpf);
          
          // Enriquecer barracas com dados dos produtos
          const enrichedTents = tents.map((tent: Barraca) => ({
            ...tent,
            lista_de_itens: tent.lista_de_itens.map((item: Item) => {
              const detalhes = mapa.get(String(item.id));
              return detalhes ? { ...detalhes, estoque: item.estoque } : { ...item, name: 'Item desc.' };
            })
          }));
          setUserTents(enrichedTents);

          // 4. Lógica de Históricos (Compras e Vendas)
          const allSales: Sale[] = Array.isArray(salesRes) ? salesRes : [];

          // Histórico de COMPRAS: Onde userCode == meu CPF
          const myPurchases = allSales.filter(s => s.userCode === user.cpf);
          setPurchaseHistory(myPurchases);

          // Histórico de VENDAS: Onde tentCode é uma das minhas barracas
          const myTentIds = tents.map((t: Barraca) => Number(t.cod_barraca));
          const mySales = allSales.filter(s => myTentIds.includes(s.tentCode));
          setSalesHistory(mySales);

        } catch (error) {
          console.error("Erro ao buscar dados", error);
        } finally {
          setLoading(false);
        }
      };

      fetchData();
    }, []);

    const handleButtonClick = () => {
      logout();
      router.push('/');
    };

    // Função auxiliar para calcular total da venda
    const calculateTotal = (items: SaleItem[]) => {
        return items.reduce((acc, item) => acc + (item.salePrice * item.saleQuantity), 0);
    };

    // Componente auxiliar para renderizar lista de vendas
    const SalesList = ({ sales, title, icon: Icon, emptyMsg, isSales }: any) => (
        <Card className="w-full">
            <CardHeader>
                <CardTitle className="flex items-center text-xl">
                    <Icon className="w-5 h-5 mr-2" />
                    {title} ({sales.length})
                </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 max-h-96 overflow-y-auto">
                {sales.length === 0 ? (
                    <p className="text-gray-500 italic">{emptyMsg}</p>
                ) : (
                    sales.map((sale: Sale) => (
                        <div key={sale.id} className="border-b pb-4 last:border-0">
                            <div className="flex justify-between items-start mb-2">
                                <div>
                                    <div className="flex items-center text-sm text-gray-500">
                                        <Calendar className="w-4 h-4 mr-1" />
                                        {/* Formatação da data */}
                                        {new Date(sale.saleDate).toLocaleDateString()}
                                    </div>
                                    <div className="text-xs text-gray-400">ID Venda: #{sale.id}</div>
                                    {isSales && <div className="text-xs font-bold text-gray-600">Barraca: #{sale.tentCode}</div>}
                                </div>
                                <div className="text-right">
                                    <span className={`font-bold text-lg ${isSales ? 'text-green-600' : 'text-red-600'}`}>
                                        {isSales ? '+ ' : '- '}
                                        R$ {calculateTotal(sale.items).toFixed(2)}
                                    </span>
                                </div>
                            </div>
                            
                            {/* Itens da venda */}
                            <div className="bg-gray-50 p-2 rounded text-sm space-y-1">
                                {sale.items.map((item, idx) => {
                                    const prod = productsMap.get(String(item.productCode));
                                    return (
                                        <div key={idx} className="flex justify-between">
                                            <span>{item.saleQuantity}x {prod?.name || `Prod #${item.productCode}`}</span>
                                            <span className="text-gray-600">R$ {item.salePrice.toFixed(2)}</span>
                                        </div>
                                    )
                                })}
                            </div>
                        </div>
                    ))
                )}
            </CardContent>
        </Card>
    );

    if (loading) return <div className="flex justify-center items-center h-screen">Carregando perfil...</div>

    return (
        <div className="min-h-screen bg-gray-50 p-4 sm:p-8 flex justify-center">
            <div className="w-full max-w-5xl space-y-8">

                {/* --- Cabeçalho do Perfil --- */}
                {userProfile && (
                    <div className="bg-white p-8 rounded-xl shadow-lg border border-gray-100 flex flex-col md:flex-row items-center md:items-start space-y-6 md:space-y-0 md:space-x-8">
                        <div className="flex-shrink-0">
                            <img
                                className="h-32 w-32 rounded-full object-cover border-4 border-indigo-200 shadow-md"
                                src={userProfile.src}
                                alt={userProfile.name}
                                onError={(e) => { (e.target as HTMLImageElement).src = "https://placehold.co/128x128?text=User" }}
                            />
                        </div>
                        <div className="text-center md:text-left flex-grow">
                            <h1 className="text-3xl font-extrabold text-gray-900">{userProfile.name}</h1>
                            <p className="mt-1 text-lg text-indigo-600 font-medium">{userProfile.login}</p>
                            <p className="mt-2 text-sm text-gray-500">CPF: {userProfile.cpf}</p>
                            <Button className="mt-4 bg-red-500 hover:bg-red-600" onClick={handleButtonClick}>
                                Sair
                            </Button>
                        </div>
                    </div>
                )}

                {/* --- Grid de Históricos --- */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Histórico de Compras (Usuário comprou) */}
                    <SalesList 
                        sales={purchaseHistory} 
                        title="Minhas Compras" 
                        icon={ShoppingBag} 
                        emptyMsg="Você ainda não realizou compras."
                        isSales={false} 
                    />

                    {/* Histórico de Vendas (Usuário vendeu nas barracas) */}
                    <SalesList 
                        sales={salesHistory} 
                        title="Vendas das Minhas Barracas" 
                        icon={DollarSign} 
                        emptyMsg="Nenhuma venda registrada em suas barracas."
                        isSales={true} 
                    />
                </div>

                {/* --- Minhas Barracas --- */}
                <div className="space-y-4">
                    <h2 className="text-2xl font-bold text-gray-800 flex items-center">
                        <Store className="w-6 h-6 text-pink-500 mr-2" />
                        Gerenciar Estoque ({userTents.length})
                    </h2>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {userTents.map((b: any) => (
                            <div key={b.cod_barraca} className="bg-white p-6 rounded-xl shadow-lg border-t-4 border-pink-500">
                                <h3 className="font-bold text-lg mb-2">{b.nome_barraca}</h3>
                                <div className="space-y-4">
                                    {b.lista_de_itens.map((p: any) => (
                                        <div key={p.id} className="flex justify-between items-center border-b pb-2 last:border-0">
                                            <div>
                                                <div className="font-semibold text-gray-900">{p.name}</div>
                                                <div className="text-sm text-gray-500">{p.description}</div>
                                            </div>
                                            <div className="text-sm bg-gray-100 px-2 py-1 rounded">
                                                Estoque: {p.estoque}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
}