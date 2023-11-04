package blog.ru.blogApp.controllers;

import blog.ru.blogApp.models.Message;
import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.models.dto.MessageDto;
import blog.ru.blogApp.security.PersonDetails;
import blog.ru.blogApp.services.MessagesService;
import blog.ru.blogApp.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Controller
@RequestMapping("/message")
public class MessageController {

    @Value("${upload.path}")
    private String uploadPath;

    private final MessagesService messagesService;


    @Autowired
    public MessageController(MessagesService messagesService){
        this.messagesService = messagesService;
    }

    @GetMapping
    public String mainPage(@ModelAttribute("message") Message message,
                       @AuthenticationPrincipal PersonDetails personDetails,
                       Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)
                           Pageable pageable
    ){

        Page<MessageDto> messages = messagesService.findAll(pageable, personDetails.getPerson());

        model.addAttribute("allMessages", messages);

        model.addAttribute("user", personDetails.getPerson());

        return "message";
    }

    private void addFile(MultipartFile file, Message message) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File((uploadPath + "/" + resultFilename)));

            message.setFilename(resultFilename);
        }
    }

    @PostMapping
    public String addMessage(
            Message message,
            @AuthenticationPrincipal PersonDetails personDetails,
            @RequestParam("file") MultipartFile file,
            BindingResult bindingResult
    ) throws IOException {


        if (bindingResult.hasErrors()){
            return "message";
        }

        addFile(file, message);

        messagesService.save(message, personDetails.getPerson());

         return "redirect:/message";
    }


    @GetMapping("/search")
    public String search(
            @AuthenticationPrincipal PersonDetails personDetails,
            Model model
    ){
        model.addAttribute("user", personDetails.getPerson().getUsername());
        return "search";
    }

    @PostMapping("/search")
    public String filterByTag(
            @AuthenticationPrincipal PersonDetails personDetails,
            @RequestParam("query") String tag,
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<MessageDto> page;


        if (tag != null && !tag.isEmpty()){
            page =  messagesService.findByTag(tag, pageable, personDetails.getPerson());
            model.addAttribute("filteredMessages", page);
        }else {
            page = messagesService.findAll(pageable, personDetails.getPerson());
            model.addAttribute("messages", page);
        }
        return "search";
    }

    @GetMapping("/my")
    public String userMessages(
            @AuthenticationPrincipal PersonDetails personDetails,
            Model model,
            @PageableDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable
    ){

        Page<MessageDto> page = messagesService.messageUserList(pageable, personDetails.getPerson());
            model.addAttribute("username", personDetails.getPerson().getUsername());
            model.addAttribute("messages", page);


        return "profile/userMessages";
    }

    @GetMapping("/my/{id}/edit")
    public String editMessage(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails,
            Model model
    ){

        Person person = messagesService.findById(id).get().getPerson();

        if (personDetails.getPerson().equals(person)){
            Message message = messagesService.findById(id).orElse(null);
            model.addAttribute("message", message);
            return "profile/editMessage";
        }

         return "profile/userMessages";
    }

    @DeleteMapping("/my/{id}")
    public String deleteMessage(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails
    ){

        Person person = messagesService.findById(id).get().getPerson();

        if (personDetails.getPerson().equals(person)){
            messagesService.delete(id);
            return "redirect:/message/my";
        }
        return "profile/editMessage";
    }

    @PatchMapping("/my/{id}/edit")
    public String updateMessage(
            @PathVariable("id") int id,
            @ModelAttribute("messageForUpdate") Message message,
            @RequestParam(value = "file", required = false) MultipartFile file,
            BindingResult bindingResult,
            @AuthenticationPrincipal PersonDetails personDetails
    ) throws IOException {


        if (bindingResult.hasErrors()){
            return "profile/editMessage";
        }

        if (!file.isEmpty()){
            addFile(file, message);
        }


        messagesService.update(id, message);
        return  "redirect:/message/my";
    }

    @GetMapping("/{id}/like")
    public String like(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ){
        messagesService.doLike(id, personDetails.getPerson());


        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();

        components.getQueryParams()
                .entrySet()
                .forEach(
                        pair ->
                        redirectAttributes.
                                addAttribute(pair.getKey(), pair.getValue()));


        return "redirect:" + components.getPath();
    }



}
