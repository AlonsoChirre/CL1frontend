package pe.edu.cibertec.autofrontendf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.autofrontendf.dto.AutoRequest;
import pe.edu.cibertec.autofrontendf.dto.AutoResponse;
import pe.edu.cibertec.autofrontendf.viewmodel.AutoModel;

@Controller
@RequestMapping("/auto")
public class AutoController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/buscar")
    public String getBuscarAuto(Model model) {
        AutoModel autoModel = new AutoModel(
                "0", "", "", "", "", "", "");
        model.addAttribute("autoModel", autoModel);
        return "auto/buscarAuto";
    }

    @PostMapping("/buscar")
    public String postBuscarAuto(
            @RequestParam("placa") String placa,
            Model model) {
        if (placa == null || placa.trim().isEmpty() || placa.length() != 7) {
            AutoModel autoModel = new AutoModel(
                    "1",
                    "Debe ingresar una placa correcta.",
                    "", "", "", "", "");
            model.addAttribute("autoModel", autoModel);
            return "auto/buscarAuto";
        }

        try {
            String endpoint = "http://localhost:8080/api/vehiculos";
            AutoRequest autoRequest = new AutoRequest(placa);
            AutoResponse autoResponse = restTemplate.postForObject(endpoint, autoRequest, AutoResponse.class);

            if (autoResponse.Codigo().equals("00")) {
                AutoModel autoModel = new AutoModel(
                        "0", "",
                        autoResponse.AutoMarca(),
                        autoResponse.AutoModelo(),
                        autoResponse.AutoNroAsientos(),
                        autoResponse.AutoPrecio(),
                        autoResponse.AutoColor());
                model.addAttribute("autoModel", autoModel);
                return "auto/detalleAuto";
            } else {
                AutoModel autoModel = new AutoModel(
                        "1",
                        "No se encontró un vehículo para la placa ingresada.",
                        "", "", "", "", "");
                model.addAttribute("autoModel", autoModel);
                return "auto/buscarAuto";
            }
        } catch(Exception e) {
            AutoModel autoModel = new AutoModel(
                    "99",
                    "Error: Ocurrió un problema al buscar el vehículo.",
                    "", "", "", "", "");
            model.addAttribute("autoModel", autoModel);
            System.out.println(e.getMessage());
            return "auto/buscarAuto";
        }
    }
}
