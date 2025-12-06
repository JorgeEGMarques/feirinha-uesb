export type product = {
  code: number;
  imagem: string | null;
  description: string | null;
  price: number;
  name: string;
  tentCode: number
};

export type profile = {
  cpf: string;
  nome: string;
  telefone: string;
  email: string;
  senha?: string;
  fotoPerfil: string | null;
};

export type comment = {
  id: number;
  texto: string;
  codProd: number;
  cpfUsuario: string;
  dataPostagem: Date | string | null;
};

export type stock = {
  productCode: number;
  tentCode: number;
  stockQuantity: number;
  product: product;
};

export type tent = {
  code: number;
  cpfHolder: string;
  name: string;
  userLicense: string | null;
  items: stock[];
};

export type BackendProduct = product;
export type BackendUser = profile;
export type BackendComment = comment;
export type BackendTent = tent;

export interface Product {
  id: number;
  name: string;
  price: number;
  description: string | null;
  imageUrl: string | null;
}

export interface UserProfile {
  cpf: string;
  name: string;
  phone: string;
  email: string;
  avatarUrl: string | null;
}

export interface TentSummary {
  code: number;
  name: string;
  ownerCpf: string;
  licenseUrl: string | null;
}

export interface ProductComment {
  id: number;
  text: string;
  productId: number;
  userCpf: string;
  userName: string;
  userAvatarUrl: string | null;
  postedAt: Date | string | null;
}