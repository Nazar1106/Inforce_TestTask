Environment Setup
Follow the instructions below to set up and run the project:

1. Clone the repository
- git clone https://github.com/Nazar1106/Inforce_TestTask.git
- cd Inforce_TestTask

2. Set up the database
To connect to the MySQL database on Aiven, configure the connection settings in the application.properties file. Add the following configurations:
- spring.datasource.url=jdbc:mysql://avnadmin:AVNS_v30vXyPCpuKNqUoj8jJ@books-scraper-kupnovytskyinazar-0c79.b.aivencloud.com:18781/defaultdb?ssl-mode=REQUIRED
- spring.datasource.username=avnadmin
- spring.datasource.password=AVNS_v30vXyPCpuKNqUoj8jJ
- scraper.start.url=https://books.toscrape.com/catalogue/category/books_1/index.html
- scraper.firstPage.url=https://books.toscrape.com/catalogue/category/books_1/

3. Install dependencies
Make sure all necessary dependencies are installed using Maven:
- mvn clean install

5. Run the application
To run the application, use the following Maven command:
- mvn spring-boot:run

5. Verify the application
After running the application, you can verify its functionality by sending requests to the local server at http://localhost:8080.
