export type product = { code: number, imagem: string | null, description: string, price: number, name: string }

export type profile = { cpf: string, nome: string, telefone: string, email: string, senha: string, fotoPerfil: string | null }

export type comment = { id: number, texto: string, codProd: number, cpfUsuario: string, dataPostagem: Date }

export type tent = { code: number, cpfHolder: string, name: string, userLicense: string, items: stock[] }

export type stock = { productCode: number, tentCode: number, stockQuantity: number, product: product }