package org.profit.telegrambot;

public class AntiHardcode {
    public static int addCounter = 1;
    public static int pageCounter = 1;
    public static String imagePath;

    public void chosePath(int pageCounter) {
        switch (pageCounter){
            case 1 ->  imagePath = "https://imgs.search.brave.com/w4_fte-fEHh-v9ENx7XVNwoBa5ZLn1tXbr_dS7qAJLY/rs:fit:400:400:1/g:ce/aHR0cDovLzQuYnAu/YmxvZ3Nwb3QuY29t/L19uaWVJR1dpQ3Nu/dy9TU1E0SHpKR0Zu/SS9BQUFBQUFBQUNF/ay82WjhKQy1TVlRR/US9zNDAwL2NoaW5h/dGVhMS5KUEc";
            case 2 -> imagePath = "https://imgs.search.brave.com/cqkyNfxneYLU8TcYepasTOrqbkisWYXt6DLzKO4E_C4/rs:fit:400:400:1/g:ce/aHR0cHM6Ly8xLmJw/LmJsb2dzcG90LmNv/bS9fbmllSUdXaUNz/bncvU1NRNEgyQjNR/R0kvQUFBQUFBQUFD/RWMvUURrQnhKeVc0/eEkvczQwMC9jaGlu/YXRlYTIuSlBH";
            case 3 -> imagePath = "https://imgs.search.brave.com/p8ndCEebKUqPjlnBo5uG9jfAWyjFqK61K0wf1D8j8UU/rs:fit:750:750:1/g:ce/aHR0cHM6Ly9pLnBp/bmltZy5jb20vb3Jp/Z2luYWxzLzFjL2Vk/LzJmLzFjZWQyZjMz/YmIyN2ZmNWUxZGJh/NTMyNWUzMzY5NjA1/LmpwZw";
        }

    }
}

