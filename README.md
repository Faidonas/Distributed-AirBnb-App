# Distributed AirBnb App

Welcome to the **Distributed AirBnb App**! This project was developed as part of a university course on Distributed Systems. It simulates a simplified version of AirBnb, where users can search for available rooms using filters, and book rooms for specified dates. The backend leverages a custom MapReduce logic with distributed storage and processing.

## Project Overview

The Distributed AirBnb App enables users to:
- Search for available rooms based on various filters.
- Book rooms by specifying available dates.

The backend architecture employs:
- **MapReduce Logic:** Rooms are distributed across three workers, and filtering requests are processed by querying all workers and aggregating results.
- **TCP Sockets:** Used for communication between master, workers, and users.

## Features

- **User Features:**
  - Filter available rooms by criteria such as location, price, and amenities.
  - View detailed information about available rooms.
  - Book rooms by specifying the desired dates.

- **Backend Features:**
  - Distributed storage of room data across three worker nodes.
  - Efficient query processing using a custom MapReduce logic.
  - Robust communication through TCP sockets between master, workers, and users.
 
## Images
<img src="Photos/Screenshot_2024-06-11-00-46-16-319_com.example.dsfrontendproject.jpg" alt="Image Description" width="200">


![Image Description]("D:\OneDrive - aueb.gr\Docs\sxoli\Year 3\Katanemimena\Photos\Screenshot_2024-06-11-00-46-28-190_com.example.dsfrontendproject.jpg")
![Image Description]("D:\OneDrive - aueb.gr\Docs\sxoli\Year 3\Katanemimena\Photos\Screenshot_2024-06-11-01-06-57-196_com.example.dsfrontendproject.jpg")


 

