# Projet E-commerce Android

Application Android de commerce électronique développée en Kotlin utilisant l'architecture MVVM et
l'API FakeStore.

## 📱 Aperçu

Cette application e-commerce offre une expérience utilisateur complète avec navigation par onglets,
gestion du panier, scanner de codes QR et système de thèmes personnalisables.

## 🏗️ Architecture

- **Pattern MVVM** (Model-View-ViewModel)
- **Navigation Component** pour la navigation entre fragments
- **Repository Pattern** pour la gestion des données
- **Coroutines** pour les opérations asynchrones
- **View Binding** pour l'accès aux vues

## 🛠️ Technologies Utilisées

### Core Android

- **Kotlin** - Langage principal
- **Android SDK 33+** - Version minimum supportée
- **Material Design Components** - Interface utilisateur moderne

### Bibliothèques Principales

- **Retrofit 2.9.0** - Client HTTP pour les appels API REST
- **Gson** - Sérialisation/désérialisation JSON
- **Glide 4.14.2** - Chargement et mise en cache d'images
- **ZXing Android Embedded 4.3.0** - Scanner de codes QR/barres
- **Kotlinx Coroutines** - Programmation asynchrone
- **OkHttp Logging Interceptor** - Logs des requêtes réseau

### Architecture Components

- **Navigation Component** - Navigation entre fragments
- **ViewModel & LiveData** - Gestion d'état réactive
- **ViewBinding** - Liaison de vues type-safe

## 🌟 Fonctionnalités Principales

### 1. 🛍️ Catalogue de Produits

- **Affichage des produits** : Liste complète des produits avec images, prix et descriptions
- **Filtrage avancé** :
    - Par catégorie (electronics, jewelery, men's clothing, women's clothing)
    - Par gamme de prix (0-50€, 50-100€, 100€+)
    - Tri par prix (croissant/décroissant) et par popularité
- **Recherche** : Barre de recherche pour trouver des produits spécifiques
- **Détail produit** : Vue détaillée avec image haute résolution, description complète et options
  d'achat

### 2. 🛒 Gestion du Panier

- **Ajout au panier** : Possibilité d'ajouter des produits depuis la liste ou le détail
- **Gestion des quantités** : Augmenter/diminuer les quantités directement dans le panier
- **Calcul automatique** : Prix total mis à jour en temps réel
- **Suppression d'articles** : Retirer des produits du panier
- **Persistance** : Le panier est sauvegardé localement

### 3. 📱 Scanner QR/Code-barres

- **Scanner intégré** : Interface de scan utilisant la caméra
- **Détection automatique** : Reconnaissance des codes QR et codes-barres
- **Mode portrait optimisé** : Interface adaptée pour une utilisation en portrait
- **Feedback visuel** : Indication claire du succès/échec du scan

### 4. 🎨 Système de Thèmes

- **Mode sombre/clair** : Basculement automatique ou manuel
- **Thèmes colorés multiples** :
    - Thème par défaut (bleu-vert)
    - Thème bleu
    - Thème vert
    - Thème violet
    - Thème orange
- **Cohérence visuelle** : Couleurs adaptées pour chaque mode (clair/sombre)
- **Persistance des préférences** : Sauvegarde des choix utilisateur

### 5. ⚙️ Paramètres

- **Gestion des thèmes** : Interface pour changer les couleurs et le mode sombre
- **Préférences utilisateur** : Sauvegarde locale des paramètres
- **Interface intuitive** : Sélecteurs visuels pour les options

### 6. 🧭 Navigation Intuitive

- **Navigation par onglets** : Barre de navigation inférieure avec 4 sections principales
- **Navigation hiérarchique** : Pile de navigation pour les détails de produits
- **Icônes expressives** : Interface claire et reconnaissable
- **Transitions fluides** : Animations entre les écrans

## 📁 Structure du Projet

```
app/src/main/java/fr/epf/min2/projet_ecommerce/
├── api/                          # Services API
│   ├── RetrofitClient.kt         # Configuration Retrofit
│   └── StoreApiService.kt        # Interface API REST
├── data/                         # Modèles de données
│   ├── Product.kt                # Modèle produit
│   ├── Cart.kt                   # Modèle panier
│   └── FilterOptions.kt          # Options de filtrage
├── repository/                   # Couche de données
│   └── StoreRepository.kt        # Repository principal
├── ui/                          # Interface utilisateur
│   ├── adapters/                # Adaptateurs RecyclerView
│   ├── cart/                    # Module panier
│   ├── products/                # Module produits
│   ├── productdetail/           # Détail produit
│   ├── scan/                    # Module scanner
│   └── settings/               # Module paramètres
├── util/                        # Utilitaires
│   └── ThemeManager.kt          # Gestionnaire de thèmes
├── App.kt                       # Classe Application
└── MainActivity.kt              # Activité principale
```

## 🎯 API et Données

### FakeStore API

L'application utilise l'API publique [FakeStore API](https://fakestoreapi.com/) pour :

- Récupération de la liste des produits
- Détails des produits individuels
- Catégories disponibles
- Gestion des paniers (simulation)

## 🎨 Design et UX

### Material Design

- Couleurs adaptatives selon le thème
- Élévations et ombres cohérentes
- Typographie standardisée

### Responsive Design

- Interface adaptée aux différentes tailles d'écran
- Mode portrait optimisé
- Gestion des états de chargement et d'erreur

### Accessibilité

- Navigation au clavier supportée
- Contrastes de couleurs conformes
- Descriptions pour les lecteurs d'écran

## 🚀 Installation et Configuration

### Prérequis

- Android Studio Arctic Fox ou plus récent
- SDK Android 33 ou supérieur
- Kotlin 1.8+

### Installation

1. Cloner le repository

```bash
git clone [URL_DU_REPOSITORY]
```

2. Ouvrir le projet dans Android Studio

3. Synchroniser les dépendances Gradle

4. Lancer l'application sur un émulateur ou appareil physique

### Configuration

Aucune configuration supplémentaire requise - l'application utilise une API publique.
