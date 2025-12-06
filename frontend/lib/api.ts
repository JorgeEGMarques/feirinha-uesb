import {
  BackendComment,
  BackendProduct,
  BackendTent,
  BackendUser,
  Product,
  ProductComment,
  TentSummary,
  UserProfile,
} from "@/utils/types"

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/crud/api"

function buildUrl(path: string) {
  return `${API_BASE_URL}${path}`
}

interface ApiError extends Error {
  status?: number
  payload?: string
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(buildUrl(path), {
    ...init,
    cache: init?.cache ?? "no-store",
    headers: {
      Accept: "application/json",
      ...(init?.headers ?? {}),
    },
  })

  if (!response.ok) {
    const payload = await response.text()
    const error: ApiError = new Error(payload || `Request failed with status ${response.status}`)
    error.status = response.status
    error.payload = payload
    throw error
  }

  return response.json()
}

const toDataUrl = (value?: string | null) => {
  if (!value) return null
  if (value.startsWith("data:")) return value
  return `data:image/jpeg;base64,${value}`
}

const normalizePrice = (value: number | string | null | undefined) => {
  if (value === null || value === undefined) return 0
  const parsed = typeof value === "number" ? value : Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

export const convertProduct = (backend: BackendProduct): Product => ({
  id: backend.code,
  name: backend.name,
  price: normalizePrice(backend.price),
  description: backend.description ?? null,
  imageUrl: toDataUrl(backend.imagem),
})

export const convertUserProfile = (backend: BackendUser): UserProfile => ({
  cpf: backend.cpf,
  name: backend.nome,
  phone: backend.telefone,
  email: backend.email,
  avatarUrl: toDataUrl(backend.fotoPerfil),
})

export const convertTent = (backend: BackendTent): TentSummary => ({
  code: backend.code,
  name: backend.name,
  ownerCpf: backend.cpfHolder,
  licenseUrl: toDataUrl(backend.userLicense),
})

export const convertProductComments = (
  comments: BackendComment[],
  users: BackendUser[]
): ProductComment[] =>
  comments.map((comment) => {
    const match = users.find((user) => user.cpf === comment.cpfUsuario)
    return {
      id: comment.id,
      text: comment.texto,
      productId: comment.codProd,
      userCpf: comment.cpfUsuario,
      userName: match?.nome ?? "Cliente",
      userAvatarUrl: match ? toDataUrl(match.fotoPerfil) : null,
      postedAt: comment.dataPostagem ?? null,
    }
  })

export async function fetchProducts(): Promise<Product[]> {
  const data = await request<BackendProduct[]>("/products")
  return data.map(convertProduct)
}

export async function fetchProductById(id: number): Promise<Product> {
  const data = await request<BackendProduct>(`/products/${id}`)
  return convertProduct(data)
}

export async function fetchProductComments(
  productId: number
): Promise<ProductComment[]> {
  const [comments, users] = await Promise.all([
    request<BackendComment[]>(`/comentarios/produto/${productId}`),
    request<BackendUser[]>("/usuarios"),
  ])
  return convertProductComments(comments, users)
}

export async function fetchBackendUsers(): Promise<BackendUser[]> {
  return request<BackendUser[]>("/usuarios")
}

export async function fetchUserProfile(cpf: string): Promise<UserProfile | null> {
  try {
    const data = await request<BackendUser>(`/usuarios/${cpf}`)
    return convertUserProfile(data)
  } catch (error: unknown) {
    const apiError = error as ApiError
    if (apiError.status === 404) {
      return null
    }
    throw error
  }
}

export async function fetchUserTents(cpf: string): Promise<TentSummary[]> {
  const tents = await request<BackendTent[]>("/tents")
  return tents.filter((tent) => tent.cpfHolder === cpf).map(convertTent)
}
